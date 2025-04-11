package com.speedhome.chatbot.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {
    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Username is required")
    private String name;

    @Pattern(regexp="^(?:\\+?60|0)1[0-46-9]\\d{7,8}$|^(?:\\+?60|0)[3-9]\\d{7,8}$")
    @NotBlank(message = "Valid mobile phone is required")
    private String mobile;

    private String role; // LANDLORD or TENANT
}