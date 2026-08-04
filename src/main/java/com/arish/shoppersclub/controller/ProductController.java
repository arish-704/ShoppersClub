package com.arish.shoppersclub.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arish.shoppersclub.dto.request.CreateProductRequest;
import com.arish.shoppersclub.dto.request.UpdateProductRequest;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductResponse;
import com.arish.shoppersclub.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ============================================================================
 * ProductController - Pagination Request Mapping
 * ============================================================================
 *
 * Pagination Query Parameters Concept:
 *
 * In REST APIs, pagination parameters are supplied via HTTP Query Parameters:
 * Example Request:
 *   GET /api/v1/products?pageNo=0&pageSize=10&sortBy=price&sortDir=asc
 *
 * Defaults:
 * - pageNo (default = "0"): First page.
 * - pageSize (default = "10"): 10 items per response payload.
 * - sortBy (default = "createdAt"): Sort by creation timestamp.
 * - sortDir (default = "desc"): Newest items first.
 * ============================================================================
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllActiveProducts(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<ProductResponse> response = productService.getAllActiveProducts(pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PagedResponse<ProductResponse>> getMyProducts(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<ProductResponse> response = productService.getMyProducts(pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {

        ProductResponse response = productService.getProductById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<ProductResponse> response = productService.getProductsByCategory(id, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/seller/{id}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsBySeller(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<ProductResponse> response = productService.getProductsBySeller(id, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }
}
