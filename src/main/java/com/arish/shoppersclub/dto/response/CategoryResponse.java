package com.arish.shoppersclub.dto.response;

import java.time.LocalDateTime;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    boolean active,
    Long parentCategoryId,
    LocalDateTime createdAt
) {

}
