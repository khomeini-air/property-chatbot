package com.speedhome.chatbot.service;

import com.speedhome.chatbot.api.dto.PropertyRequest;
import com.speedhome.chatbot.api.dto.PropertyResponse;

import java.util.List;

public interface PropertyService {
    PropertyResponse addProperty(Long userId, PropertyRequest request);
    List<PropertyResponse> getPropertiesByUser(Long userId);
}
