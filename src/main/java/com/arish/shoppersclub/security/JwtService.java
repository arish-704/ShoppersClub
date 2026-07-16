package com.arish.shoppersclub.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.entity.User;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Creates the cryptographic key used to sign and verify JWTs.
     *
     * The key is loaded from the application configuration
     * and converted into an HMAC SHA key understood by JJWT.
     *
     * @return signing key
     */
    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // --> converts the BASE64 string into raw bytes
        return Keys.hmacShaKeyFor(keyBytes); // --> // Convert the byte array into a SecretKey used for HMAC signing.
    }
    
    /**
     * Generates a signed JWT for the authenticated user.
     *
     * The user's email is stored as the JWT subject (sub),
     * along with the issued time and expiration time.
     *
     * @param user authenticated user
     * @return signed JWT token
     */
    public String generateToken(User user){
        return Jwts.builder()
                    .subject(user.getEmail()) //--> subject is the identity of the token owner
                    .issuedAt(new Date(System.currentTimeMillis())) // --> every jwt should know the time it was created
                    .expiration(
                        new Date(
                            System.currentTimeMillis() + jwtExpiration
                        )
                    )
                    .signWith(getSigningKey())
                    .compact(); // --> builder is not finished until we say compact()

    }
}
