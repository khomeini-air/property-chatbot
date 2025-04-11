package com.speedhome.chatbot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.speedhome.chatbot.entity.Property;
import com.speedhome.chatbot.repository.PropertyRepository;
import com.speedhome.chatbot.repository.UserRepository;
import com.speedhome.chatbot.service.AppointmentService;
import com.speedhome.chatbot.entity.Appointment;
import com.speedhome.chatbot.entity.User;
import com.speedhome.chatbot.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.speedhome.chatbot.util.Constant.ADDRESS;
import static com.speedhome.chatbot.util.Constant.DATETIME;
import static com.speedhome.chatbot.util.Constant.LANDLORD_EMAIL;
import static com.speedhome.chatbot.util.Constant.PROPERTY_ID;
import static com.speedhome.chatbot.util.Constant.STATUS;
import static com.speedhome.chatbot.util.Constant.TENANT_EMAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Transactional
    public Appointment processAppointment(String aiResponse) {
        Map<String, String> collectedData = extractAppointmentData(aiResponse);

        validateData(collectedData);

        Property property = propertyRepository.findById(Long.parseLong(collectedData.get(PROPERTY_ID)))
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        User tenant = userRepository.findByEmail(collectedData.get(TENANT_EMAIL))
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));


        Appointment appointment = Appointment.builder()
                .dateTime(LocalDateTime.parse(collectedData.get(DATETIME)))
                .property(property)
                .landlord(property.getUser())
                .tenant(tenant)
                .confirmed(true)
                .reminderSent(false)
                .build();

        appointmentRepository.save(appointment);
        log.info("Created appointment: {}", appointment);

        return appointment;
    }

    private Map<String, String> extractAppointmentData(String aiResponse) {
        try {
            Map<String, String> data = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonResponse = objectMapper.readTree(aiResponse);
            Iterator<Map.Entry<String, JsonNode>> fields = jsonResponse.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                data.put(entry.getKey(), entry.getValue().asText());
            }

            return data;
        } catch (IOException e) {
            String errorMessage = "Error parsing confirmed AI Response";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Validates required appointment fields
     */
    private void validateData(Map<String, String> data) {
        if (!data.containsKey(STATUS)) {
            throw new IllegalArgumentException("Missing status");
        }
        if (!data.containsKey(DATETIME)) {
            throw new IllegalArgumentException("Missing appointment time");
        }
        if (!data.containsKey(PROPERTY_ID)) {
            throw new IllegalArgumentException("Missing property");
        }
        if (!data.containsKey(ADDRESS)) {
            throw new IllegalArgumentException("Missing address");
        }
        if (!data.containsKey(LANDLORD_EMAIL)) {
            throw new IllegalArgumentException("Missing landlord phone");
        }
        if (!data.containsKey(TENANT_EMAIL)) {
            throw new IllegalArgumentException("Missing tenant phone");
        }
    }
}