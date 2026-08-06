package com.arish.shoppersclub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arish.shoppersclub.dto.request.CreateProductImageRequest;
import com.arish.shoppersclub.dto.response.ProductImageResponse;
import com.arish.shoppersclub.service.ProductImageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/products/{productId}/images")
@RequiredArgsConstructor
@Validated
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductImageResponse> addImage(
            @PathVariable Long productId,
            @Valid @RequestBody CreateProductImageRequest request) {

        ProductImageResponse response = productImageService.addImage(productId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getProductImages(
            @PathVariable Long productId) {

        List<ProductImageResponse> response = productImageService.getProductImages(productId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{imageId}/primary")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductImageResponse> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        ProductImageResponse response = productImageService.setPrimaryImage(productId, imageId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SELLER')")
    public void deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        productImageService.deleteImage(productId, imageId);
    }
}
