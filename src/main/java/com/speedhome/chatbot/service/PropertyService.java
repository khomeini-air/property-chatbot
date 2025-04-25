package com.speedhome.chatbot.service;

import com.speedhome.chatbot.api.request.PropertyRequest;
import com.speedhome.chatbot.api.response.PropertyResponse;

import java.util.List;

public interface PropertyService {
    PropertyResponse addProperty(Long userId, PropertyRequest request);
    List<PropertyResponse> getPropertiesByUser(Long userId);
}
