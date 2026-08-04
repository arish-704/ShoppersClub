package com.arish.shoppersclub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.arish.shoppersclub.enums.ProductStatus;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    ProductStatus status,
    Long sellerId,
    Long categoryId,
    LocalDateTime createdAt
) {

}
