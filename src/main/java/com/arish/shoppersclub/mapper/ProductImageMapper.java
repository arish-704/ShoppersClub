package com.arish.shoppersclub.mapper;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.request.CreateProductImageRequest;
import com.arish.shoppersclub.dto.response.ProductImageResponse;
import com.arish.shoppersclub.entity.ProductImage;

@Component
public class ProductImageMapper {

    public ProductImage toEntity(CreateProductImageRequest request) {
        Integer order = request.displayOrder() != null ? request.displayOrder() : 1;
        return ProductImage.builder()
                .imageUrl(request.imageUrl())
                .isPrimary(request.isPrimary())
                .displayOrder(order)
                .build();
    }

    public ProductImageResponse toResponse(ProductImage productImage) {
        Long productId = productImage.getProduct() != null ? productImage.getProduct().getId() : null;

        return new ProductImageResponse(
                productImage.getId(),
                productImage.getImageUrl(),
                productImage.isPrimary(),
                productImage.getDisplayOrder(),
                productId,
                productImage.getCreatedAt()
        );
    }
}
