package com.speedhome.chatbot.api.controller;

import com.speedhome.chatbot.api.dto.ApiResponse;
import com.speedhome.chatbot.api.dto.PropertyRequest;
import com.speedhome.chatbot.api.dto.PropertyResponse;
import com.speedhome.chatbot.entity.User;
import com.speedhome.chatbot.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse> addProperty(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PropertyRequest request) {

        User user = (User) userDetails;
        PropertyResponse response = propertyService.addProperty(user.getId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "Property added", response));
    }
}
