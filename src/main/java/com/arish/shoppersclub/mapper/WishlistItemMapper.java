package com.arish.shoppersclub.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.response.WishlistItemResponse;
import com.arish.shoppersclub.entity.Product;
import com.arish.shoppersclub.entity.ProductImage;
import com.arish.shoppersclub.entity.WishlistItem;
import com.arish.shoppersclub.enums.ProductStatus;
import com.arish.shoppersclub.repository.ProductImageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WishlistItemMapper {

    private final ProductImageRepository productImageRepository;

    public WishlistItemResponse toResponse(WishlistItem wishlistItem) {
        Product product = wishlistItem.getProduct();

        Long productId = product != null ? product.getId() : null;
        String productName = product != null ? product.getName() : "Unknown Product";
        BigDecimal productPrice = product != null ? product.getPrice() : BigDecimal.ZERO;
        ProductStatus productStatus = product != null ? product.getStatus() : null;

        String productImageUrl = null;
        if (product != null) {
            productImageUrl = productImageRepository.findByProductAndIsPrimaryTrue(product)
                    .map(ProductImage::getImageUrl)
                    .orElseGet(() -> productImageRepository.findByProduct(product).stream()
                            .findFirst()
                            .map(ProductImage::getImageUrl)
                            .orElse(null));
        }

        return new WishlistItemResponse(
                wishlistItem.getId(),
                productId,
                productName,
                productPrice,
                productImageUrl,
                productStatus,
                wishlistItem.getCreatedAt()
        );
    }
}
