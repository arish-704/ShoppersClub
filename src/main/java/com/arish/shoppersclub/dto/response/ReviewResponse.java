package com.arish.shoppersclub.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long productId,
    String productName,
    String reviewerFirstName,
    Integer rating,
    String comment,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
