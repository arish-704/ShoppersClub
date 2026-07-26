package com.arish.shoppersclub.dto.request;

import com.arish.shoppersclub.enums.AddressType;

public record UpdateAddressRequest(
    String fullName,
    String phoneNumber,
    String addressLine1,
    String addressLine2,
    String city,
    String state,
    String country,
    String postalCode,
    AddressType addressType
) {

}
