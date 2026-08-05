package com.arish.shoppersclub.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateSellerVerificationRequest(
    @NotNull(message = "Verification status is required")
    Boolean verified
) {

}
