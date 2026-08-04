package com.arish.shoppersclub.mapper;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.request.CreateProductRequest;
import com.arish.shoppersclub.dto.request.UpdateProductRequest;
import com.arish.shoppersclub.dto.response.ProductResponse;
import com.arish.shoppersclub.entity.Product;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build();
    }

    public void updateEntity(Product product, UpdateProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
    }

    public ProductResponse toResponse(Product product) {
        Long sellerId = product.getSeller() != null ? product.getSeller().getId() : null;
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                sellerId,
                categoryId,
                product.getCreatedAt()
        );
    }
}
