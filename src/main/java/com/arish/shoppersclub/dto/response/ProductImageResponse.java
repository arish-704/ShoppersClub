package com.arish.shoppersclub.dto.response;

import java.time.LocalDateTime;

public record ProductImageResponse(
    Long id,
    String imageUrl,
    boolean isPrimary,
    Integer displayOrder,
    Long productId,
    LocalDateTime createdAt
) {

}
