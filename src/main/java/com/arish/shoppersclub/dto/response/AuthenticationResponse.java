package com.arish.shoppersclub.dto.response;

/**
 * Response returned after successful authentication.
 *
 * Contains the generated JSON Web Token (JWT) that
 * the client must include in subsequent requests.
 *
 * @param token generated JWT
 */
public record AuthenticationResponse(

    String token
) {

}
