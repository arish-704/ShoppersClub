package com.arish.shoppersclub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arish.shoppersclub.dto.request.AdminProductStatusRequest;
import com.arish.shoppersclub.dto.request.UpdateOrderStatusRequest;
import com.arish.shoppersclub.dto.request.UpdateSellerVerificationRequest;
import com.arish.shoppersclub.dto.response.AdminPlatformOverviewResponse;
import com.arish.shoppersclub.dto.response.OrderResponse;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductResponse;
import com.arish.shoppersclub.dto.response.SellerResponse;
import com.arish.shoppersclub.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/sellers/{sellerId}/verify")
    public ResponseEntity<SellerResponse> verifySeller(
            @PathVariable Long sellerId,
            @Valid @RequestBody UpdateSellerVerificationRequest request) {

        SellerResponse response = adminService.verifySeller(sellerId, request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse response = adminService.updateOrderStatus(orderId, request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/products/{productId}/status")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable Long productId,
            @Valid @RequestBody AdminProductStatusRequest request) {

        ProductResponse response = adminService.updateProductStatus(productId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products")
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<ProductResponse> response = adminService.getAllProducts(pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<PagedResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<OrderResponse> response = adminService.getAllOrders(pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sellers")
    public ResponseEntity<PagedResponse<SellerResponse>> getAllSellers(
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<SellerResponse> response = adminService.getAllSellers(verified, pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<PagedResponse<com.arish.shoppersclub.dto.response.UserProfileResponse>> getAllUsers(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir) {

        PagedResponse<com.arish.shoppersclub.dto.response.UserProfileResponse> response = adminService.getAllUsers(pageNo, pageSize, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/overview")
    public ResponseEntity<AdminPlatformOverviewResponse> getPlatformOverview() {

        AdminPlatformOverviewResponse response = adminService.getPlatformOverview();

        return ResponseEntity.ok(response);
    }
}
