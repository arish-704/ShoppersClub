package com.arish.shoppersclub.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
    Long id,
    Long productId,
    String productName,
    String productImageUrl,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal
) {

}
