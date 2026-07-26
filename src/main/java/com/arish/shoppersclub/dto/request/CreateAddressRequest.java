package com.arish.shoppersclub.dto.request;

import com.arish.shoppersclub.enums.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAddressRequest(
    @NotBlank
    String fullName,

    @NotBlank
    String phoneNumber,

    @NotBlank
    String addressLine1,

    String addressLine2,

    @NotBlank
    String city,

    @NotBlank
    String state,

    @NotBlank
    String country,

    @NotBlank
    String postalCode,

    @NotNull
    AddressType addressType

) {

}
