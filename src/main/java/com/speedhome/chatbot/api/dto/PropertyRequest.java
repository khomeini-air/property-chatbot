package com.speedhome.chatbot.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRequest {
    @NotBlank(message = "Address is required")
    private String address;
}
