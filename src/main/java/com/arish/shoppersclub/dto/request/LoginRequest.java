package com.arish.shoppersclub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;



/**
 * Request payload used for user authentication.
 *
 * @param email user's registered email
 * @param password user's password
 */
public record LoginRequest(

    @NotNull(message = "Email is required !")
    @Email(message = "Invalid email format")
    String email,

    @NotNull(message = "Password is required !")
    String password
) {}
