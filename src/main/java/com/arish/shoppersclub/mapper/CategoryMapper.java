package com.arish.shoppersclub.mapper;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.request.CreateCategoryRequest;
import com.arish.shoppersclub.dto.request.UpdateCategoryRequest;
import com.arish.shoppersclub.dto.response.CategoryResponse;
import com.arish.shoppersclub.entity.Category;

@Component
public class CategoryMapper {

    public Category toEntity(CreateCategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public void updateEntity(Category category, UpdateCategoryRequest request) {
        category.setName(request.name());
        category.setDescription(request.description());
    }

    public CategoryResponse toResponse(Category category) {

        Long parentCategoryId = category.getParentCategory() != null
                ? category.getParentCategory().getId()
                : null;

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                parentCategoryId,
                category.getCreatedAt()
        );
    }
}