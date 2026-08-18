package com.mekonnen.commercial_lending_platform.controller;

import com.mekonnen.commercial_lending_platform.dto.LoginRequest;
import com.mekonnen.commercial_lending_platform.dto.LoginResponse;
import com.mekonnen.commercial_lending_platform.entity.EmployeeRole;
import com.mekonnen.commercial_lending_platform.exception.GlobalExceptionHandler;
import com.mekonnen.commercial_lending_platform.exception.InactiveEmployeeException;
import com.mekonnen.commercial_lending_platform.exception.InvalidCredentialsException;
import com.mekonnen.commercial_lending_platform.service.AuthService;

import com.mekonnen.commercial_lending_platform.service.JwtService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void login_shouldReturn200ForValidCredentials() throws Exception {

        LoginResponse response = new LoginResponse(
                "test-jwt-token",
                "Bearer",
                3600000L
        );

        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "john.smith@example.com",
                                            "password": "Password123!"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("test-jwt-token"))
                .andExpect(jsonPath("$.tokenType")
                        .value("Bearer"))
                .andExpect(jsonPath("$.expiresIn")
                        .value(3600000));

    }

    @Test
    void login_shouldReturn401ForInvalidCredentials() throws Exception {

        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException(
                        "Invalid email or password."
                ));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "john.smith@example.com",
                                            "password": "WrongPassword!"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password."));
    }

    @Test
    void login_shouldReturn403ForInactiveEmployee() throws Exception {

        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new InactiveEmployeeException(
                        "Employee account is inactive."
                ));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "john.smith@example.com",
                                            "password": "Password123!"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message")
                        .value("Employee account is inactive."));
    }

    @Test
    void login_shouldReturn400ForInvalidRequest() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "",
                                            "password": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }
}