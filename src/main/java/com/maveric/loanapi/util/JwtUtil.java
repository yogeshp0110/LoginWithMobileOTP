package com.maveric.loanapi.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key to sign the JWT token (use a stronger key in production)
    private String SECRET_KEY = "8bfae9e73db23073d97df476e776a6e890b746f0e0efdd445d8f2b62779b1734";

    // Using base64 encoded key
    private byte[] encodedSecretKey;

    public JwtUtil() {
        // Base64 encode the secret key for signing and verification
        this.encodedSecretKey = Base64.getDecoder().decode(SECRET_KEY);
    }

    // Method to generate JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // 1 hour expiration
                .signWith(SignatureAlgorithm.HS256, encodedSecretKey)  // Sign the token with the encoded secret key
                .compact();
    }

    // Extract username (subject) from the JWT token
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(encodedSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract expiration date from the token
    private Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(encodedSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate the JWT token
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
