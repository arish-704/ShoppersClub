package com.arish.shoppersclub.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.CreateProductRequest;
import com.arish.shoppersclub.dto.request.UpdateProductRequest;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductResponse;
import com.arish.shoppersclub.entity.Category;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.Seller;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.enums.ProductStatus;
import com.arish.shoppersclub.exception.CategoryNotFoundException;
import com.arish.shoppersclub.exception.ProductNotFoundException;
import com.arish.shoppersclub.exception.SellerNotFoundException;
import com.arish.shoppersclub.exception.SellerNotVerifiedException;
import com.arish.shoppersclub.mapper.ProductMapper;
import com.arish.shoppersclub.repository.CategoryRepository;
import com.arish.shoppersclub.repository.ProductRepository;
import com.arish.shoppersclub.repository.SellerRepository;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.ProductService;

import lombok.RequiredArgsConstructor;

/**
 * ============================================================================
 * ProductServiceImpl - Pagination Concept Implementation
 * ============================================================================
 *
 * Pagination Concepts Applied:
 *
 * 1. PageRequest.of(pageNo, pageSize, Sort.by(...))
 *    Creates a Pageable object that tells Spring Data JPA:
 *    - Which page number to fetch (0-indexed)
 *    - How many items per page
 *    - Field to sort by (e.g. "createdAt", "price") and direction ("asc" / "desc")
 *
 * 2. Page<Product>
 *    Returned by the repository. Holds both the list of records for the requested
 *    page and metadata (total rows, total pages, etc.).
 *
 * 3. PagedResponse<ProductResponse>
 *    Maps Page<Product> into an immutable DTO record returned to the controller.
 * ============================================================================
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Seller seller = getAuthenticatedSeller();

        if (!seller.isVerified()) {
            throw new SellerNotVerifiedException("Only verified sellers are allowed to create products");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + request.categoryId()));

        Product product = productMapper.toEntity(request);
        product.setSeller(seller);
        product.setCategory(category);

        if (request.stock() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else {
            product.setStatus(ProductStatus.ACTIVE);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public PagedResponse<ProductResponse> getMyProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Seller seller = getAuthenticatedSeller();
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findBySeller(seller, pageable);
        return mapToPagedResponse(productPage);
    }

    /**
     * Fetches product details by ID.
     * Cached in Redis with key "products::<id>" for high-performance repeat reads.
     */
    @Override
    @org.springframework.cache.annotation.Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }

    /**
     * Updates an existing product.
     * Automatically evicts (flushes) the stale product entry from Redis cache.
     */
    @Override
    @org.springframework.cache.annotation.CacheEvict(value = "products", key = "#id")
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Seller seller = getAuthenticatedSeller();

        Product product = productRepository.findByIdAndSeller(id, seller)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (request.status() == ProductStatus.OUT_OF_STOCK) {
            throw new IllegalArgumentException("Sellers cannot manually set status to OUT_OF_STOCK");
        }

        productMapper.updateEntity(product, request);

        if (!product.getCategory().getId().equals(request.categoryId())) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + request.categoryId()));
            product.setCategory(category);
        }

        if (request.stock() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (request.status() != null) {
            product.setStatus(request.status());
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    /**
     * Soft-deletes a product (sets status to INACTIVE).
     * Evicts the deleted product entry from Redis cache so stale data is not served.
     */
    @Override
    @org.springframework.cache.annotation.CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        Seller seller = getAuthenticatedSeller();
        Product product = productRepository.findByIdAndSeller(id, seller)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    public PagedResponse<ProductResponse> getProductsByCategory(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findByCategory(category, pageable);
        return mapToPagedResponse(productPage);
    }

    @Override
    public PagedResponse<ProductResponse> getProductsBySeller(Long sellerId, int pageNo, int pageSize, String sortBy, String sortDir) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + sellerId));
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findBySeller(seller, pageable);
        return mapToPagedResponse(productPage);
    }

    @Override
    public PagedResponse<ProductResponse> getAllActiveProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> productPage = productRepository.findByStatus(ProductStatus.ACTIVE, pageable);
        return mapToPagedResponse(productPage);
    }

    /**
     * Helper method to build a Pageable instance from raw pagination parameters.
     *
     * @param pageNo   Zero-based page index (0 = first page)
     * @param pageSize Number of records per page
     * @param sortBy   Entity field name to sort by (e.g. "createdAt", "price")
     * @param sortDir  Sort direction ("asc" or "desc")
     * @return Configured Pageable object for Spring Data JPA
     */
    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(pageNo, pageSize, sort);
    }

    /**
     * Helper method to map a Spring Data Page<Product> into our custom PagedResponse<ProductResponse> DTO.
     */
    private PagedResponse<ProductResponse> mapToPagedResponse(Page<Product> productPage) {
        List<ProductResponse> content = productPage.getContent().stream()
                .map(productMapper::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    private Seller getAuthenticatedSeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return sellerRepository.findByUser(user)
                .orElseThrow(() -> new SellerNotFoundException("Seller profile not found for the authenticated user"));
    }
}
