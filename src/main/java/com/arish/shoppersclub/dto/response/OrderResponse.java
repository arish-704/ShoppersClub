package com.arish.shoppersclub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.arish.shoppersclub.enums.OrderStatus;
import com.arish.shoppersclub.enums.PaymentStatus;

public record OrderResponse(
    Long id,
    OrderStatus orderStatus,
    PaymentStatus paymentStatus,
    BigDecimal totalAmount,
    Integer totalItems,
    String fullName,
    String phoneNumber,
    String addressLine1,
    String addressLine2,
    String city,
    String state,
    String country,
    String postalCode,
    List<OrderItemResponse> items,
    LocalDateTime createdAt
) {

}
