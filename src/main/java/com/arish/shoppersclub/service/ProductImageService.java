package com.arish.shoppersclub.service;

import java.util.List;

import com.arish.shoppersclub.dto.request.CreateProductImageRequest;
import com.arish.shoppersclub.dto.response.ProductImageResponse;

public interface ProductImageService {

    ProductImageResponse addImage(Long productId, CreateProductImageRequest request);

    List<ProductImageResponse> getProductImages(Long productId);

    ProductImageResponse setPrimaryImage(Long productId, Long imageId);

    void deleteImage(Long productId, Long imageId);

}
