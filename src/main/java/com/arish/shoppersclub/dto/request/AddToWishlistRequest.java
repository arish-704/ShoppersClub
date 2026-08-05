package com.arish.shoppersclub.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddToWishlistRequest(
    @NotNull(message = "Product ID is required")
    Long productId
) {

}
