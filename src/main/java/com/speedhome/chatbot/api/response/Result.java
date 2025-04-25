package com.speedhome.chatbot.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Result {
    SUCCESS("S", "SUCCESS", "Success"),
    BAD_CREDENTIALS("F", "UNAUTHORIZED", "Invalid email/password"),
    EMAIL_EXISTS("F", "EMAIL_EXIST", "Email already exist"),
    BAD_APPOINTMENT("F", "BAD_APPOINTMENT", "Invalid Appointment"),
    PARAM_ILLEGAL("F", "PARAM_ILLEGAL", "Bad Parameter"),
    INTERNAL_ERROR("F", "UNKNOWN_ERROR", "Unknown Error");

    private final String result;
    private final String code;
    private final String description;
}
