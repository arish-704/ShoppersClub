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

/**
 * Spring Security filter responsible for authenticating incoming
 * HTTP requests using a JSON Web Token (JWT).
 *
 * The filter extracts the JWT from the Authorization header,
 * validates it, and stores the authenticated user in the
 * SecurityContext so that downstream components know who
 * is making the request.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;


    /**
     * Authenticates an incoming HTTP request using the JWT present
     * in the Authorization header.
     *
     * If a valid Bearer token is found, the authenticated user is
     * stored in the Spring Security context before the request
     * continues through the remaining filter chain.
     *
     * @param request incoming HTTP request
     * @param response outgoing HTTP response
     * @param filterChain remaining filters in the security chain
     * @throws ServletException if the request cannot be processed
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
                final String authHeader = request.getHeader("Authorization");

                if(authHeader == null || !authHeader.startsWith("Bearer ")){
                    filterChain.doFilter(request, response);
                    return;
                }

                
                try {

    final String jwt = authHeader.substring(7);
    final String username = jwtService.extractUsername(jwt);

    if (username != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authenticationToken);
        }
    }

} catch (JwtException | IllegalArgumentException ex) {

    SecurityContextHolder.clearContext();

}

filterChain.doFilter(request, response);
            }

    

}
