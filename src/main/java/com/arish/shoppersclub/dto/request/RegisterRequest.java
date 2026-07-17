package com.arish.shoppersclub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank
    String firstName,
    String lastName,

    @NotBlank
    @Email
    String email,

    @NotBlank
    @Size(min = 8)
    String password
) {

}
