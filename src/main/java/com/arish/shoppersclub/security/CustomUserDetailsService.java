package com.arish.shoppersclub.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.repository.UserRepository;

import lombok.RequiredArgsConstructor;




/**
 * Loads a user from the database using the supplied username.
 *
 * In this application, the username represents the user's
 * email address. Spring Security invokes this method during
 * the authentication process to retrieve the authenticated user.
 *
 * @param username the email address of the user attempting to log in
 * @return the authenticated user's details
 * @throws UsernameNotFoundException if no user exists with the given email
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        return userRepository.findByEmail(username)
                             .orElseThrow(() -> new UsernameNotFoundException("User not found!!"));
    }

}
