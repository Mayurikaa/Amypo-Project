package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utility class for JWT token operations.
 * Provides helper methods for extracting information from JWT tokens
 * and resolving tokens from HTTP requests.
 */
@Component
public class JwtUtil {

    private final Key signingKey;

    public JwtUtil(@Value("${app.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts the Bearer token from the Authorization header.
     *
     * @param request the HTTP request
     * @return the token without "Bearer " prefix, or null if not found
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Extracts the Bearer token from an Authorization header string.
     *
     * @param authHeader the Authorization header value
     * @return the token without "Bearer " prefix, or null if not found
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token the JWT token
     * @return the Claims object containing all token claims
     */
    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the subject (email) from a JWT token.
     *
     * @param token the JWT token
     * @return the subject (email) claim
     */
    public String getEmailFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    /**
     * Extracts the role from a JWT token.
     *
     * @param token the JWT token
     * @return the role claim
     */
    public String getRoleFromToken(String token) {
        return (String) getAllClaims(token).get("role");
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return getAllClaims(token).getExpiration();
    }

    /**
     * Checks if a JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = getExpirationDateFromToken(token);
            return expirationDate.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Checks if a JWT token is valid (not expired and not malformed).
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            getAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the issued-at date from a JWT token.
     *
     * @param token the JWT token
     * @return the issued-at date
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getAllClaims(token).getIssuedAt();
    }

    /**
     * Gets the claims key as a specific type from a JWT token.
     *
     * @param token the JWT token
     * @param claimsResolver a function to extract the desired claim
     * @param <T> the type of the claim
     * @return the claim value
     */
    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts a custom claim from a JWT token.
     *
     * @param token the JWT token
     * @param claimName the name of the claim
     * @return the claim value as a String, or null if not found
     */
    public String getCustomClaim(String token, String claimName) {
        try {
            return (String) getAllClaims(token).get(claimName);
        } catch (Exception e) {
            return null;
        }
    }
}
