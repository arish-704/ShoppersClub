package com.arish.shoppersclub.dto.request;


import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
    @NotBlank
    String name,

    String description,

    Long parentCategoryId
) {
    
}
