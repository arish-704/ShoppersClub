package com.arish.shoppersclub.dto.response;

import java.time.LocalDateTime;

import com.arish.shoppersclub.enums.AddressType;

public record AddressResponse(
    Long id,
    String fullName,
    String phoneNumber,
    String addressLine1,
    String addressLine2,
    String city,
    String state,
    String country,
    String postalCode,
    AddressType addressType,
    boolean defaultAddress,
    LocalDateTime createdAt
) {

}
