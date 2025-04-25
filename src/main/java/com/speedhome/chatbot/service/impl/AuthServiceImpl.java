package com.speedhome.chatbot.service.impl;

import com.speedhome.chatbot.api.response.AuthResponse;
import com.speedhome.chatbot.api.request.UserLoginRequest;
import com.speedhome.chatbot.api.request.UserRegisterRequest;
import com.speedhome.chatbot.api.response.Result;
import com.speedhome.chatbot.entity.User;
import com.speedhome.chatbot.exception.EmailExistsException;
import com.speedhome.chatbot.repository.UserRepository;
import com.speedhome.chatbot.service.AuthService;
import com.speedhome.chatbot.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(UserRegisterRequest request) {
        validateExist(request);

        User user = User.builder()
                .username(request.getName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);
        log.info("New user created successfully. User Email: {}", user.getEmail());

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .result(Result.SUCCESS)
                .token(jwtToken)
                .email(user.getUsername())
                .role(user.getRole().name())
                .message("New user created")
                .build();
    }

    private void validateExist(UserRegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailExistsException(String.format("Email %s already exists", request.getEmail()));
        }
    }

    public AuthResponse login(UserLoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        log.info("User Logged in successfully. User Email: {}", user.getEmail());

        return AuthResponse.builder()
                .result(Result.SUCCESS)
                .token(jwtToken)
                .email(user.getUsername())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }
}