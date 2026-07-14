package com.arish.shoppersclub.dto;

import java.util.Set;

import com.arish.shoppersclub.enums.Role;

public record RegisterResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Set<Role> roles
    
) {

}
