package com.arish.shoppersclub.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.dto.response.UserProfileResponse;
import com.arish.shoppersclub.entity.User;
import com.arish.shoppersclub.mapper.UserMapper;
import com.arish.shoppersclub.repository.UserRepository;
import com.arish.shoppersclub.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserProfileResponse getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //fetches that context for the current request and getAuthentication(): Asks the context, "Who is currently authenticated?"
        User user = (User) authentication.getPrincipal();
        return userMapper.toProfileResponse(user);
    }

}
