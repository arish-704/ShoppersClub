package com.arish.shoppersclub.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.response.OrderItemResponse;
import com.arish.shoppersclub.dto.response.OrderResponse;
import com.arish.shoppersclub.entity.Order;
import com.arish.shoppersclub.entity.OrderItem;

@Component
public class OrderMapper {

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        Long productId = orderItem.getProduct() != null ? orderItem.getProduct().getId() : null;

        return new OrderItemResponse(
                orderItem.getId(),
                productId,
                orderItem.getProductName(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getSubtotal()
        );
    }

    public OrderResponse toOrderResponse(Order order, List<OrderItemResponse> items) {
        return new OrderResponse(
                order.getId(),
                order.getOrderStatus(),
                order.getPaymentStatus(),
                order.getTotalAmount(),
                order.getTotalItems(),
                order.getFullName(),
                order.getPhoneNumber(),
                order.getAddressLine1(),
                order.getAddressLine2(),
                order.getCity(),
                order.getState(),
                order.getCountry(),
                order.getPostalCode(),
                items,
                order.getCreatedAt()
        );
    }
}
