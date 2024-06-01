package com.TaskManagement.TaskFlow.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskFlow.Exception.ExpiredTokenException;
import com.TaskManagement.TaskFlow.Exception.InvalidTokenException;
import com.TaskManagement.TaskFlow.Exception.TokenValidationException;

import java.security.Key;


@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    



    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Change here

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Set the subject of the token
                .signWith(secretKey) // Sign the token with the secret key
                .compact(); // Compact the token
    }

    public boolean validateToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
    
            // Additional checks (if needed):
            // - Verify user existence based on claims (e.g., email).
            // - Check other custom claims (if any).
    
            return true;
        } catch (ExpiredJwtException e) {
            // Handle expired token
            throw new ExpiredTokenException("The token has expired. Please log in again.");
        } catch (SignatureException e) {
            // Handle invalid signature
            throw new InvalidTokenException("The token signature is invalid.");
        } catch (JwtException e) {
            // Log other exceptions for debugging
            throw new TokenValidationException("Token validation failed. Please try again later.");
        }
    }
    
    public String extractEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
