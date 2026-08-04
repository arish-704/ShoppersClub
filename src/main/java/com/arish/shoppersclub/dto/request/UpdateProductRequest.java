package com.arish.shoppersclub.dto.request;

import java.math.BigDecimal;

import com.arish.shoppersclub.enums.ProductStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateProductRequest(
    @NotBlank(message = "Product name is required")
    String name,

    @NotBlank(message = "Product description is required")
    String description,

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    BigDecimal price,

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    Integer stock,

    @NotNull(message = "Category ID is required")
    Long categoryId,

    ProductStatus status
) {

}
