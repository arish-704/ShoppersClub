package com.arish.shoppersclub.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// import com.arish.shoppersclub.entity.User;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
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
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                    .subject(userDetails.getUsername()) //--> subject is the identity of the token owner
                    .issuedAt(new Date(System.currentTimeMillis())) // --> every jwt should know the time it was created
                    .expiration(
                        new Date(
                            System.currentTimeMillis() + jwtExpiration
                        )
                    )
                    .signWith(getSigningKey())
                    .compact(); // --> builder is not finished until we say compact()

                }


    /**
     * Extracts all claims contained in the JWT.
     *
     * This method verifies the token's digital signature using
     * the configured signing key before returning its payload.
     * If the signature is invalid or the token has been tampered
     * with, an exception is thrown.
     *
     * @param token JWT received from the client
     * @return all claims stored inside the token
     */
    private Claims extractAllClaims(String token){ 
        return Jwts
                   .parser() // I want to parse a JWT.
                   .verifyWith(getSigningKey()) // Use my secret key.
                   .build() // Finish configuring the parser.
                   .parseSignedClaims(token) // Parse this signed JWT.
                   .getPayload(); // give me the claims
    }



    /**
     * Extracts a specific claim from the JWT.
     *
     * This method first retrieves all claims from the token and
     * then applies the supplied function to obtain the required
     * claim.
     *
     * It serves as a reusable helper for extracting values such
     * as the subject, expiration time, or any custom claim.
     *
     * @param token JWT received from the client
     * @param claimsResolver function used to select the desired claim
     * @param <T> type of the claim being returned
     * @return extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(token);  // extract claims --> apply function --> return result
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
}
