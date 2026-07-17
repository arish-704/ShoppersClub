package com.arish.shoppersclub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final CustomUserDetailsService customUserDetailsService;


    /**
     * Creates the password encoder used to hash and verify user passwords.
     *
     * BCrypt is chosen because it is adaptive, salted, and widely
     * recommended for securely storing passwords.
     *
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    /**
     * Configures the authentication provider responsible for
     * validating user credentials against the application's database.
     *
     * It delegates user retrieval to the CustomUserDetailsService
     * and password verification to the configured PasswordEncoder.
     *
     * @return configured DAO authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    /**
     * Exposes Spring Security's AuthenticationManager as a bean.
     *
     * The AuthenticationManager coordinates the authentication
     * process by delegating authentication requests to the
     * configured AuthenticationProvider.
     *
     * @param configuration Spring Security authentication configuration
     * @return application authentication manager
     * @throws Exception if the authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

}
