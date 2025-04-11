package com.speedhome.chatbot.api.controller;

import com.speedhome.chatbot.api.dto.UserLoginRequest;
import com.speedhome.chatbot.api.dto.UserRegisterRequest;
import com.speedhome.chatbot.api.dto.AuthResponse;
import com.speedhome.chatbot.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
