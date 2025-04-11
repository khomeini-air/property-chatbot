package com.speedhome.chatbot.service;

import com.speedhome.chatbot.entity.ChatMessage;

import java.util.List;

public interface AiService {
    String generateResponse(String sessionId, List<ChatMessage> messages);
}
