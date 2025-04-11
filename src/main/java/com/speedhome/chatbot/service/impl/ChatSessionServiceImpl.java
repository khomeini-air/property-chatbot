package com.speedhome.chatbot.service.impl;

import com.speedhome.chatbot.entity.ChatMessage;
import com.speedhome.chatbot.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

    // Stores ongoing conversations where Key=sessionId, Value=chat history
    private final Map<String, List<ChatMessage>> sessions = new ConcurrentHashMap<>();

    // Tracks last activity time for session cleanup
    private final Map<String, LocalDateTime> lastActivity = new ConcurrentHashMap<>();

    // Timeout after 10 minutes of inactivity
    private static final long SESSION_TIMEOUT_MINUTES = 10;

    @Value("${ai.deepseek.chat.context}")
    private String context;

    @Override
    public void addMessage(String sessionId, String role, String content) {
        cleanExpiredSessions();

        List<ChatMessage> messages = sessions.computeIfAbsent(sessionId, k -> new ArrayList<>()
        );

        messages.add(new ChatMessage(role, content));
        lastActivity.put(sessionId, LocalDateTime.now());

        log.debug("Added message to session {}: {}: {}", sessionId, role, content);
    }

    @Override
    public List<ChatMessage> getConversationHistory(String sessionId) {
        cleanExpiredSessions();

        List<ChatMessage> messages = sessions.getOrDefault(sessionId, new ArrayList<>());

        // Prepend system prompt if new session
        if (messages.isEmpty() || !"system".equals(messages.get(0).getRole())) {
            messages.add(0, new ChatMessage("system", context));
        }

        return messages;
    }

    @Override
    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
        lastActivity.remove(sessionId);
        log.info("Completed session {}", sessionId);
    }

    /**
     * Removes sessions older than SESSION_TIMEOUT_MINUTES
     */
    private void cleanExpiredSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(SESSION_TIMEOUT_MINUTES);

        lastActivity.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(cutoff)) {
                sessions.remove(entry.getKey());
                log.debug("Cleaned up expired session {}", entry.getKey());
                return true;
            }
            return false;
        });
    }
}
