package com.arish.shoppersclub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arish.shoppersclub.dto.request.LoginRequest;
import com.arish.shoppersclub.dto.request.RegisterRequest;
import com.arish.shoppersclub.dto.response.AuthenticationResponse;
import com.arish.shoppersclub.dto.response.RegisterResponse;
import com.arish.shoppersclub.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request){

        RegisterResponse response = authService.register(request);

        return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request){
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity
               .status(HttpStatus.OK)
               .body(response);
    }


}
