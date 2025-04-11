package com.speedhome.chatbot.service;

import com.speedhome.chatbot.entity.ChatMessage;

import java.util.List;

public interface ChatSessionService {

    /**
     * Adds a new message to the session history
     */
    void addMessage(String sessionId, String role, String content);

    /**
     * Gets the conversation history with system prompt
     */
    List<ChatMessage> getConversationHistory(String sessionId);

    /**
     * Clears completed sessions
     */
    void clearSession(String sessionId);
}
