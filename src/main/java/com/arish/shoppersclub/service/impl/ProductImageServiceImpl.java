package com.arish.shoppersclub.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.CreateProductImageRequest;
import com.arish.shoppersclub.dto.response.ProductImageResponse;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.ProductImage;
import com.arish.shoppersclub.entity.Seller;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.exception.DuplicateProductImageException;
import com.arish.shoppersclub.exception.ProductImageNotFoundException;
import com.arish.shoppersclub.exception.ProductNotFoundException;
import com.arish.shoppersclub.exception.SellerNotFoundException;
import com.arish.shoppersclub.mapper.ProductImageMapper;
import com.arish.shoppersclub.repository.ProductImageRepository;
import com.arish.shoppersclub.repository.ProductRepository;
import com.arish.shoppersclub.repository.SellerRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.ProductImageService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.multipart.MultipartFile;
import com.arish.shoppersclub.service.ImageStorageService;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final ProductImageMapper productImageMapper;
    private final ImageStorageService imageStorageService;

    /**
     * Uploads a MultipartFile to Cloudinary, receives the hosted secure URL, and persists the ProductImage entity.
     */
    @Override
    public ProductImageResponse addImage(Long productId, MultipartFile file, boolean isPrimary) {
        Product product = getOwnedProduct(productId);

        String imageUrl = imageStorageService.uploadImage(file);

        if (productImageRepository.existsByProductAndImageUrl(product, imageUrl)) {
            throw new DuplicateProductImageException("Product image with URL '" + imageUrl + "' already exists for this product");
        }

        ProductImage productImage = ProductImage.builder()
                .imageUrl(imageUrl)
                .isPrimary(false)
                .displayOrder(1)
                .product(product)
                .build();

        boolean hasImages = productImageRepository.existsByProduct(product);

        if (!hasImages) {
            productImage.setPrimary(true);
        } else if (isPrimary) {
            unsetCurrentPrimary(product);
            productImage.setPrimary(true);
        }

        ProductImage savedImage = productImageRepository.save(productImage);
        return productImageMapper.toResponse(savedImage);
    }

    @Override
    public List<ProductImageResponse> getProductImages(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        List<ProductImage> images = productImageRepository.findByProductOrderByDisplayOrderAsc(product);
        return images.stream()
                .map(productImageMapper::toResponse)
                .toList();
    }

    @Override
    public ProductImageResponse setPrimaryImage(Long productId, Long imageId) {
        Product product = getOwnedProduct(productId);

        ProductImage productImage = productImageRepository.findByIdAndProduct(imageId, product)
                .orElseThrow(() -> new ProductImageNotFoundException("Product image not found with id: " + imageId));

        if (!productImage.isPrimary()) {
            unsetCurrentPrimary(product);
            productImage.setPrimary(true);
            productImage = productImageRepository.save(productImage);
        }

        return productImageMapper.toResponse(productImage);
    }

    /**
     * Deletes a product image both from Cloudinary storage and the database.
     * Auto-promotes the next remaining image to primary if the deleted image was primary.
     */
    @Override
    public void deleteImage(Long productId, Long imageId) {
        Product product = getOwnedProduct(productId);

        ProductImage productImage = productImageRepository.findByIdAndProduct(imageId, product)
                .orElseThrow(() -> new ProductImageNotFoundException("Product image not found with id: " + imageId));

        boolean wasPrimary = productImage.isPrimary();
        String imageUrl = productImage.getImageUrl();

        // 1. Delete image from Cloudinary storage
        imageStorageService.deleteImage(imageUrl);

        // 2. Delete entity from database
        productImageRepository.delete(productImage);

        // 3. Auto-promote next remaining image to primary if needed
        if (wasPrimary) {
            List<ProductImage> remainingImages = productImageRepository.findByProductOrderByDisplayOrderAsc(product);
            if (!remainingImages.isEmpty()) {
                ProductImage newPrimary = remainingImages.get(0);
                newPrimary.setPrimary(true);
                productImageRepository.save(newPrimary);
            }
        }
    }

    private void unsetCurrentPrimary(Product product) {
        Optional<ProductImage> existingPrimary = productImageRepository.findByProductAndIsPrimaryTrue(product);
        if (existingPrimary.isPresent()) {
            ProductImage primary = existingPrimary.get();
            primary.setPrimary(false);
            productImageRepository.save(primary);
        }
    }

    private Product getOwnedProduct(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new SellerNotFoundException("Seller profile not found for the authenticated user"));

        return productRepository.findByIdAndSeller(productId, seller)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
    }
}
