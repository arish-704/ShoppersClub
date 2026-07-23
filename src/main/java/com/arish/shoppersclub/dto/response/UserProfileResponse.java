package com.arish.shoppersclub.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import com.arish.shoppersclub.enums.Role;

public record UserProfileResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Set<Role> roles,
    LocalDateTime createdAt
) {

}
