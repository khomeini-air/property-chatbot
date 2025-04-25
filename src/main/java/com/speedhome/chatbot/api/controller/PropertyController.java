package com.speedhome.chatbot.api.controller;

import com.speedhome.chatbot.api.request.PropertyRequest;
import com.speedhome.chatbot.api.response.PropertyResponse;
import com.speedhome.chatbot.api.response.Result;
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
    public ResponseEntity<PropertyResponse> addProperty(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PropertyRequest request) {

        User user = (User) userDetails;
        PropertyResponse response = propertyService.addProperty(user.getId(), request);
        return ResponseEntity.ok(PropertyResponse.builder()
                .result(Result.SUCCESS)
                .id(response.getId())
                .ownerId(response.getOwnerId())
                .address(response.getAddress())
                .message("Property added")
                .build());
    }
}
