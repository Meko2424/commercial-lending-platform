package com.mekonnen.commercial_lending_platform.service;

import com.mekonnen.commercial_lending_platform.config.JwtProperties;
import com.mekonnen.commercial_lending_platform.entity.Employee;
import com.mekonnen.commercial_lending_platform.entity.EmployeeRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "this-is-a-test-secret-key-that-is-long-enough-for-hs256";

    private JwtService jwtService;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {

        JwtProperties properties = new JwtProperties();
        properties.setSecret(SECRET);
        properties.setExpirationMs(3600000);

        jwtService = new JwtService(properties);

        signingKey = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void generateToken_shouldContainEmployeeInformation() {

        Employee employee = new Employee();

        employee.setId(UUID.randomUUID());
        employee.setFirstName("John");
        employee.setLastName("Smith");
        employee.setEmail("john.smith@example.com");
        employee.setRole(EmployeeRole.ANALYST);
        employee.setActive(true);

        String token = jwtService.generateToken(employee);

        assertNotNull(token);
        assertFalse(token.isBlank());

        Jws<Claims> parsedToken = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);

        Claims claims = parsedToken.getPayload();

        assertEquals(
                employee.getId().toString(),
                claims.getSubject()
        );

        assertEquals(
                employee.getEmail(),
                claims.get("email", String.class)
        );

        assertEquals(
                employee.getRole().name(),
                claims.get("role", String.class)
        );

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateToken_shouldCreateValidExpiration() {

        Employee employee = new Employee();

        employee.setId(UUID.randomUUID());
        employee.setFirstName("John");
        employee.setLastName("Smith");
        employee.setEmail("john.smith@example.com");
        employee.setRole(EmployeeRole.ANALYST);
        employee.setActive(true);

        String token = jwtService.generateToken(employee);

        Jws<Claims> parsedToken = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);

        Claims claims = parsedToken.getPayload();

        assertTrue(
                claims.getExpiration().after(claims.getIssuedAt())
        );
    }

    @Test
    void validateToken_shouldReturnClaimsForValidToken() {
        Employee employee = new Employee();

        employee.setId(UUID.randomUUID());
        employee.setEmail("john.smith@example.com");
        employee.setRole(EmployeeRole.ANALYST);

        String token = jwtService.generateToken(employee);

        Claims claims = jwtService.validateToken(token);

        assertNotNull(claims);
        assertEquals(employee.getId().toString(), claims.getSubject());
        assertEquals(employee.getEmail(), claims.get("email"));
        assertEquals(
                employee.getRole().name(),
                claims.get("role")
        );
    }

    @Test
    void validateToken_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertThrows(
                Exception.class,
                () -> jwtService.validateToken(invalidToken)
        );
    }
}