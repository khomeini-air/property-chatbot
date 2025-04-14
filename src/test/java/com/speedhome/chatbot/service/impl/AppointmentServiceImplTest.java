package com.speedhome.chatbot.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.speedhome.chatbot.entity.Appointment;
import com.speedhome.chatbot.entity.Property;
import com.speedhome.chatbot.entity.Role;
import com.speedhome.chatbot.entity.User;
import com.speedhome.chatbot.repository.AppointmentRepository;
import com.speedhome.chatbot.repository.PropertyRepository;
import com.speedhome.chatbot.repository.UserRepository;
import com.speedhome.chatbot.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ContextConfiguration(classes = {AppointmentServiceImpl.class})
@ExtendWith(SpringExtension.class)
class AppointmentServiceImplTest {
    @MockBean
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    @MockBean
    private PropertyRepository propertyRepository;

    @MockBean
    private UserRepository userRepository;

    private final String validJsonResponse = """
        {
            "status": "confirmed",
            "datetime": "2023-12-20T14:00",
            "propertyId": "1",
            "address": "123 Main St",
            "landlordEmail": "landlord@x.com",
            "tenantEmail": "tenant@y.com"
        }
        """;

    private final String invalidJsonResponse = "invalid json";
    private final String aiResponseInvalidTime = """
        {
            "datetime": "2023-12-20 14:00"
        }
        """;

    @Test
    void processAppointment_WithValidData_ReturnsAppointment() {
        // Arrange
        User landlord = User.builder().id(1L).email("landlord@x.com").mobile("+6012332231").role(Role.LANDLORD).password("123").build();
        User tenant = User.builder().id(2L).email("tenant@y.com").mobile("+60190967576").role(Role.TENANT).password("123").build();

        Property property = Property.builder().id(1L).user(landlord).build();

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(userRepository.findByEmail("tenant@y.com")).thenReturn(Optional.of(tenant));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Appointment result = appointmentService.processAppointment(validJsonResponse);

        // Assert
        assertNotNull(result);
        assertEquals(LocalDateTime.parse("2023-12-20T14:00"), result.getDateTime());
        assertEquals(1L, result.getProperty().getId());
        assertEquals(2L, result.getTenant().getId());
        assertTrue(result.isConfirmed());
        assertFalse(result.isReminderSent());

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void processAppointment_WithInvalidJson_ThrowsException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> appointmentService.processAppointment(invalidJsonResponse));

        assertEquals("Error parsing confirmed AI Response", exception.getMessage());
    }

    @Test
    void processAppointment_WithMissingProperty_ThrowsException() throws JsonProcessingException {
        // Arrange
        when(propertyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.processAppointment(validJsonResponse));

        assertEquals("Property not found", exception.getMessage());
    }

    @Test
    void processAppointment_WithMissingTenant_ThrowsException() throws JsonProcessingException {
        // Arrange
        Property mockProperty = new Property();
        mockProperty.setId(1L);

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(mockProperty));
        when(userRepository.findByEmail("tenant@y.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.processAppointment(validJsonResponse));

        assertEquals("Tenant not found", exception.getMessage());
    }

    @Test
    void validateData_WithInvalidDateTime_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> appointmentService.processAppointment(validJsonResponse));
    }
}
