package com.arish.shoppersclub.dto.request;

import com.arish.shoppersclub.enums.OrderStatus;
import com.arish.shoppersclub.enums.PaymentStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull(message = "Order status is required")
    OrderStatus orderStatus,

    PaymentStatus paymentStatus
) {

}
