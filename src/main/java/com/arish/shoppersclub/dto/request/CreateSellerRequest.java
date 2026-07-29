package com.arish.shoppersclub.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSellerRequest(
    @NotBlank
    String storeName,

    String description,

    @NotBlank
    String phoneNumber,

    String gstNumber
) {

}
