package com.speedhome.chatbot.service;

import com.speedhome.chatbot.api.dto.UserLoginRequest;
import com.speedhome.chatbot.api.dto.UserRegisterRequest;
import com.speedhome.chatbot.api.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(UserRegisterRequest request);
    AuthResponse login(UserLoginRequest request);
}
