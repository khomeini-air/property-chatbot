package com.speedhome.chatbot.handler;

import com.speedhome.chatbot.api.dto.ApiResponse;
import com.speedhome.chatbot.exception.EmailExistsException;
import com.speedhome.chatbot.exception.InvalidAppointmentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, "Invalid email/password", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailExist(EmailExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, "User email already exists", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAppointmentException.class)
    public ResponseEntity<ApiResponse> handleInvalidAppointment(InvalidAppointmentException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Invalid appointment", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(new ApiResponse(false, "Unexpected error", ex.getMessage()));
    }
}
