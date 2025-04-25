package com.speedhome.chatbot.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.speedhome.chatbot.api.response.ApiResponse;
import com.speedhome.chatbot.api.response.Result;
import com.speedhome.chatbot.exception.EmailExistsException;
import com.speedhome.chatbot.exception.InvalidAppointmentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder().result(Result.BAD_CREDENTIALS).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder().result(Result.PARAM_ILLEGAL).message(ex.getMessage()).build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleInvalidEnumValue(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();

        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType().isEnum()) {
                message = String.format("Invalid value '%s' for %s. Allowed values: %s",
                        ife.getValue(),
                        ife.getTargetType().getSimpleName(),
                        Arrays.toString(ife.getTargetType().getEnumConstants()));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder().result(Result.PARAM_ILLEGAL).message(message).build());
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailExist(EmailExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.builder().result(Result.EMAIL_EXISTS).message(ex.getMessage()).build());
    }

    @ExceptionHandler(InvalidAppointmentException.class)
    public ResponseEntity<ApiResponse> handleInvalidAppointment(InvalidAppointmentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.builder().result(Result.BAD_APPOINTMENT).message(ex.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        log.error(String.format("Internal Error: %s", ex.fillInStackTrace()));
        return ResponseEntity.internalServerError()
                .body(ApiResponse.builder().result(Result.INTERNAL_ERROR).message(ex.getMessage()).build());
    }
}
