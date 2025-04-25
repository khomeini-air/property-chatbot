package com.speedhome.chatbot.entity;

import lombok.Getter;

@Getter
public enum Role {
    LANDLORD("ROLE_LANDLORD"),
    TENANT("ROLE_TENANT"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }
}