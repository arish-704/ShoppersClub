package com.arish.shoppersclub.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import com.arish.shoppersclub.service.RedisService;

/**
 * Spring Security filter responsible for authenticating incoming
 * HTTP requests using a JSON Web Token (JWT).
 *
 * The filter extracts the JWT from the Authorization header,
 * checks Redis to ensure the token has not been blacklisted (logged out),
 * validates it, and stores the authenticated user in the SecurityContext.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();

        // Bypass JWT authentication filter completely for public authentication endpoints
        if (requestURI.startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ") && !authHeader.endsWith("null") && !authHeader.endsWith("undefined")) {
            final String jwt = authHeader.substring(7);
            String redisKey = "blacklist:token:" + jwt;

            // Only attempt authentication if the token is NOT in Redis blacklist
            if (!redisService.hasKey(redisKey)) {
                try {
                    final String username = jwtService.extractUsername(jwt);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                            authenticationToken.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );

                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                } catch (JwtException | IllegalArgumentException ex) {
                    SecurityContextHolder.clearContext();
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
