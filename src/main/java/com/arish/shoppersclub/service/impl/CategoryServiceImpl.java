package com.arish.shoppersclub.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.CreateCategoryRequest;
import com.arish.shoppersclub.dto.request.UpdateCategoryRequest;
import com.arish.shoppersclub.dto.response.CategoryResponse;
import com.arish.shoppersclub.entity.Category;
import com.arish.shoppersclub.exception.CategoryAlreadyExistsException;
import com.arish.shoppersclub.exception.CategoryNotFoundException;
import com.arish.shoppersclub.mapper.CategoryMapper;
import com.arish.shoppersclub.repository.CategoryRepository;
import com.arish.shoppersclub.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException("Category with name '" + request.name() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);

        if (request.parentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found with id: " + request.parentCategoryId()));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findByActiveTrue();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (!category.getName().equalsIgnoreCase(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException("Category with name '" + request.name() + "' already exists");
        }

        categoryMapper.updateEntity(category, request);

        if (request.parentCategoryId() != null) {
            if (id.equals(request.parentCategoryId())) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }
            Category parentCategory = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Parent category not found with id: " + request.parentCategoryId()));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        category.setActive(false);
        categoryRepository.save(category);
    }
}
