package com.arish.shoppersclub.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.arish.shoppersclub.dto.response.ProductImageResponse;

public interface ProductImageService {

    ProductImageResponse addImage(Long productId, MultipartFile file, boolean isPrimary);

    List<ProductImageResponse> getProductImages(Long productId);

    ProductImageResponse setPrimaryImage(Long productId, Long imageId);

    void deleteImage(Long productId, Long imageId);

}
