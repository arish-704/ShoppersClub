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

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    public ProductImageResponse addImage(Long productId, CreateProductImageRequest request) {
        Product product = getOwnedProduct(productId);

        if (productImageRepository.existsByProductAndImageUrl(product, request.imageUrl())) {
            throw new DuplicateProductImageException("Product image with URL '" + request.imageUrl() + "' already exists for this product");
        }

        ProductImage productImage = productImageMapper.toEntity(request);
        productImage.setProduct(product);

        boolean hasImages = productImageRepository.existsByProduct(product);

        if (!hasImages) {
            productImage.setPrimary(true);
        } else if (request.isPrimary()) {
            unsetCurrentPrimary(product);
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

    @Override
    public void deleteImage(Long productId, Long imageId) {
        Product product = getOwnedProduct(productId);

        ProductImage productImage = productImageRepository.findByIdAndProduct(imageId, product)
                .orElseThrow(() -> new ProductImageNotFoundException("Product image not found with id: " + imageId));

        boolean wasPrimary = productImage.isPrimary();

        productImageRepository.delete(productImage);

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
