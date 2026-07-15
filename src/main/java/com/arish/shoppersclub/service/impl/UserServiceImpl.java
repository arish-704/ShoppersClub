package com.arish.shoppersclub.service.impl;

import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.RegisterRequest;
import com.arish.shoppersclub.dto.RegisterResponse;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.enums.Role;
import com.arish.shoppersclub.exception.EmailAlreadyExistsException;
import com.arish.shoppersclub.mapper.UserMapper;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.UserService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public RegisterResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("This Email Already Exists in the database.");
        }
        
            User user = userMapper.toEntity(request);
            user.getRoles().add(Role.CUSTOMER);
            user.setActive(true);
            userRepository.save(user);
            return userMapper.toResponse(user);
        
    }
}
