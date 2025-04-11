package com.speedhome.chatbot.service.impl;

import com.speedhome.chatbot.api.dto.PropertyRequest;
import com.speedhome.chatbot.api.dto.PropertyResponse;
import com.speedhome.chatbot.entity.Property;
import com.speedhome.chatbot.entity.User;
import com.speedhome.chatbot.repository.PropertyRepository;
import com.speedhome.chatbot.repository.UserRepository;
import com.speedhome.chatbot.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyResponse addProperty(Long userId, PropertyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = Property.builder()
                .address(request.getAddress())
                .user(user)
                .build();

        user.addProperty(property);
        Property savedProperty = propertyRepository.save(property);

        return PropertyResponse.builder()
                .id(savedProperty.getId())
                .address(savedProperty.getAddress())
                .ownerId(user.getId())
                .build();
    }

    public List<PropertyResponse> getPropertiesByUser(Long userId) {
        return propertyRepository.findByUserId(userId)
                .stream()
                .map(p -> PropertyResponse.builder()
                        .id(p.getId())
                        .address(p.getAddress())
                        .ownerId(p.getUser().getId())
                        .build())
                .toList();
    }
}
