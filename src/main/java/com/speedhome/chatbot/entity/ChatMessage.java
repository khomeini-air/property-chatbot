package com.speedhome.chatbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessage {
    private String role; // "system", "user", or "assistant"
    private String content;
}
