package com.arish.shoppersclub.service;

import java.util.List;

import com.arish.shoppersclub.dto.request.CreateCategoryRequest;
import com.arish.shoppersclub.dto.request.UpdateCategoryRequest;
import com.arish.shoppersclub.dto.response.CategoryResponse;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void deleteCategory(Long id);

}
