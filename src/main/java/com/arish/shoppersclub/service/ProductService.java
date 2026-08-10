package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.CreateProductRequest;
import com.arish.shoppersclub.dto.request.UpdateProductRequest;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    PagedResponse<ProductResponse> getMyProducts(int pageNo, int pageSize, String sortBy, String sortDir);

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    PagedResponse<ProductResponse> getProductsByCategory(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir);

    PagedResponse<ProductResponse> getProductsBySeller(Long sellerId, int pageNo, int pageSize, String sortBy, String sortDir);

    PagedResponse<ProductResponse> getAllActiveProducts(String search, int pageNo, int pageSize, String sortBy, String sortDir);

}
