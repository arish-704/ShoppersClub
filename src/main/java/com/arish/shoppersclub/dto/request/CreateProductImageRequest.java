package com.arish.shoppersclub.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateProductImageRequest(
    @NotBlank(message = "Image URL is required")
    String imageUrl,

    boolean isPrimary,

    @Min(value = 1, message = "Display order must be at least 1")
    Integer displayOrder
) {

}
