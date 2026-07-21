package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.LoginRequest;
import com.arish.shoppersclub.dto.request.RegisterRequest;
import com.arish.shoppersclub.dto.response.AuthenticationResponse;
import com.arish.shoppersclub.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    AuthenticationResponse login(LoginRequest request);
}
