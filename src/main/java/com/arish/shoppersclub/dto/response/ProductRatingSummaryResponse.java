package com.arish.shoppersclub.dto.response;

public record ProductRatingSummaryResponse(
    Long productId,
    Double averageRating,
    Long totalReviews
) {

}
