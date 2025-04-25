package com.speedhome.chatbot.service;

import com.speedhome.chatbot.api.request.UserLoginRequest;
import com.speedhome.chatbot.api.request.UserRegisterRequest;
import com.speedhome.chatbot.api.response.AuthResponse;

public interface AuthService {
    AuthResponse register(UserRegisterRequest request);
    AuthResponse login(UserLoginRequest request);
}
