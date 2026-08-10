package com.arish.shoppersclub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.arish.shoppersclub.enums.ProductStatus;

public record WishlistItemResponse(
    Long id,
    Long productId,
    String productName,
    BigDecimal productPrice,
    String productImageUrl,
    ProductStatus productStatus,
    LocalDateTime createdAt
) {

}
