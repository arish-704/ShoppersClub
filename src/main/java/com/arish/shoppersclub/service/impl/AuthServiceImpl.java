package com.arish.shoppersclub.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.request.LoginRequest;
import com.arish.shoppersclub.dto.request.RegisterRequest;
import com.arish.shoppersclub.dto.response.AuthenticationResponse;
import com.arish.shoppersclub.dto.response.RegisterResponse;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.enums.Role;
import com.arish.shoppersclub.exception.EmailAlreadyExistsException;
import com.arish.shoppersclub.mapper.UserMapper;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.security.CustomUserDetailsService;
import com.arish.shoppersclub.security.JwtService;
import com.arish.shoppersclub.service.AuthService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Override
    public RegisterResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("This Email Already Exists in the database.");
        }
        
            User user = userMapper.toEntity(request);
            user.setPassword(passwordEncoder.encode(request.password()));
            user.getRoles().add(Role.CUSTOMER);
            user.setActive(true);
            userRepository.save(user);
            return userMapper.toResponse(user);
        
    }

    /**
     * Authenticates a user using the supplied email and password.
     *
     * The authentication process is delegated to Spring Security's
     * {@link AuthenticationManager}, which verifies the credentials
     * using the configured {@link org.springframework.security.authentication.AuthenticationProvider},
     * {@link CustomUserDetailsService}, and {@link org.springframework.security.crypto.password.PasswordEncoder}.
     *
     * Upon successful authentication, the authenticated user's
     * details are extracted from the returned {@link Authentication}
     * object and used to generate a JSON Web Token (JWT).
     *
     * @param request login request containing the user's email and password
     * @return an {@link AuthenticationResponse} containing the generated JWT
     *
     * @throws org.springframework.security.authentication.BadCredentialsException
     *         if the supplied credentials are invalid
     */
    @Override
    public AuthenticationResponse login(LoginRequest request) {
       final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
       final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
       final String token = jwtService.generateToken(userDetails);
       return new AuthenticationResponse(token);

    }
}
