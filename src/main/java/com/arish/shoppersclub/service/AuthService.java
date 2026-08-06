package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.LoginRequest;
import com.arish.shoppersclub.dto.request.RegisterRequest;
import com.arish.shoppersclub.dto.response.AuthenticationResponse;
import com.arish.shoppersclub.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    AuthenticationResponse login(LoginRequest request);

    /**
     * Invalidates the provided Bearer token by saving it to Redis with a TTL equal to its remaining lifespan.
     *
     * @param bearerToken HTTP Authorization header value (e.g., "Bearer eyJhbGci...")
     */
    void logout(String bearerToken);
}
