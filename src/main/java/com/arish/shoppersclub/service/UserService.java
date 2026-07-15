package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.RegisterRequest;
import com.arish.shoppersclub.dto.RegisterResponse;

public interface UserService {
    RegisterResponse register(RegisterRequest request);

}
