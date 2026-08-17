package com.mekonnen.commercial_lending_platform.controller;

import com.mekonnen.commercial_lending_platform.dto.LoginRequest;
import com.mekonnen.commercial_lending_platform.dto.LoginResponse;
import com.mekonnen.commercial_lending_platform.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.authenticate(request);

        return ResponseEntity.ok(response);
    }
}
