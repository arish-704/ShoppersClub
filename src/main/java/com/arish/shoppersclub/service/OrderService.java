package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.CreateOrderRequest;
import com.arish.shoppersclub.dto.response.OrderResponse;
import com.arish.shoppersclub.dto.response.PagedResponse;

public interface OrderService {

    OrderResponse placeOrder(CreateOrderRequest request);

    PagedResponse<OrderResponse> getMyOrders(int pageNo, int pageSize, String sortBy, String sortDir);

    OrderResponse getOrderById(Long id);

    OrderResponse cancelOrder(Long id);

}
