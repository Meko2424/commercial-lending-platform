package com.mekonnen.commercial_lending_platform.service;


import com.mekonnen.commercial_lending_platform.config.JwtProperties;
import com.mekonnen.commercial_lending_platform.entity.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        this.signingKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(Employee employee) {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime()
                        + jwtProperties.getExpirationMs()
        );

        return Jwts.builder()
                .subject(employee.getId().toString())
                .claim("email", employee.getEmail())
                .claim("role", employee.getRole().name())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public long getExpirationMs() {
        return jwtProperties.getExpirationMs();
    }

    public Claims validateToken(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}