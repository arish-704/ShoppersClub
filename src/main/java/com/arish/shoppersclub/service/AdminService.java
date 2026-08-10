package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.AdminProductStatusRequest;
import com.arish.shoppersclub.dto.request.UpdateOrderStatusRequest;
import com.arish.shoppersclub.dto.request.UpdateSellerVerificationRequest;
import com.arish.shoppersclub.dto.response.AdminPlatformOverviewResponse;
import com.arish.shoppersclub.dto.response.OrderResponse;
import com.arish.shoppersclub.dto.response.PagedResponse;
import com.arish.shoppersclub.dto.response.ProductResponse;
import com.arish.shoppersclub.dto.response.SellerResponse;

public interface AdminService {

    SellerResponse verifySeller(Long sellerId, UpdateSellerVerificationRequest request);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    ProductResponse updateProductStatus(Long productId, AdminProductStatusRequest request);

    PagedResponse<ProductResponse> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);

    PagedResponse<OrderResponse> getAllOrders(int pageNo, int pageSize, String sortBy, String sortDir);

    PagedResponse<SellerResponse> getAllSellers(Boolean verified, int pageNo, int pageSize, String sortBy, String sortDir);

    PagedResponse<com.arish.shoppersclub.dto.response.UserProfileResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    AdminPlatformOverviewResponse getPlatformOverview();

}
