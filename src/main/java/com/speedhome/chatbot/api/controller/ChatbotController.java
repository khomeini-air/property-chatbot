package com.speedhome.chatbot.api.controller;

import com.speedhome.chatbot.api.dto.ApiResponse;
import com.speedhome.chatbot.api.dto.ChatRequest;
import com.speedhome.chatbot.entity.Appointment;
import com.speedhome.chatbot.entity.ChatMessage;
import com.speedhome.chatbot.service.AiService;
import com.speedhome.chatbot.service.AppointmentService;
import com.speedhome.chatbot.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatSessionService sessionService;
    private final AiService aiService;
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LANDLORD', 'TENANT')")
    public ResponseEntity<ApiResponse> chat(@RequestHeader("X-Session-ID") String sessionId,
                                            @RequestBody ChatRequest chatRequest) {
        // Store user message
        sessionService.addMessage(sessionId, "user", chatRequest.getMessage());

        // Get full conversation history
        List<ChatMessage> messages = sessionService.getConversationHistory(sessionId);

        // Generate AI response based on session message
        String aiResponse = aiService.generateResponse(sessionId, messages);

        // Check if response contains appointment details and parse them
        if (aiResponse.contains("Status: CONFIRMED")) {
            sessionService.clearSession(sessionId);
            Appointment appointment = appointmentService.processAppointment(aiResponse);
            return ResponseEntity.ok(new ApiResponse(true, "Success", getConfirmedMessage(appointment)));
        }

        return ResponseEntity.ok(new ApiResponse(true, "Success", aiResponse));
    }

    private String getConfirmedMessage(Appointment appointment) {
        return String.format("Your booking is confirmed. \nLanlord: %s. \nProperty Address: %s",
                appointment.getLandlord().getUsername(),
                appointment.getProperty().getAddress());
    }
}
