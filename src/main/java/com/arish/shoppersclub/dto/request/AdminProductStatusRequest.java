package com.arish.shoppersclub.dto.request;

import com.arish.shoppersclub.enums.ProductStatus;

import jakarta.validation.constraints.NotNull;

public record AdminProductStatusRequest(
    @NotNull(message = "Product status is required")
    ProductStatus status
) {

}
