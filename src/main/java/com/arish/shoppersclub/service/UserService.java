package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.RegisterRequest;
import com.arish.shoppersclub.dto.response.RegisterResponse;

public interface UserService {
    RegisterResponse register(RegisterRequest request);

}
