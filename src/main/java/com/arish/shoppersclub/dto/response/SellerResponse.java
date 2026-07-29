package com.arish.shoppersclub.dto.response;

import java.time.LocalDateTime;

public record SellerResponse(
    Long id,
    String storeName,
    String description,
    String phoneNumber,
    String gstNumber,
    boolean verified,
    LocalDateTime createdAt
) {

}
