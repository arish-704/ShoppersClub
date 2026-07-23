package com.arish.shoppersclub.mapper;

import org.springframework.stereotype.Component;

import com.arish.shoppersclub.dto.request.RegisterRequest;
import com.arish.shoppersclub.dto.response.RegisterResponse;
import com.arish.shoppersclub.dto.response.UserProfileResponse;
import com.arish.shoppersclub.entity.User;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request){
        return User.builder()
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .email(request.email())
                    .password(request.password())
                    .build();
    }

    public RegisterResponse toResponse(User user){
        return new RegisterResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRoles()
        );
    }

    public UserProfileResponse toProfileResponse(User user){
        return new UserProfileResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRoles(),
            user.getCreatedAt()
        );
    }

}
