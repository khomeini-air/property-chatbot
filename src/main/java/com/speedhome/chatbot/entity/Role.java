package com.speedhome.chatbot.entity;

public enum Role {
    LANDLORD("ROLE_LANDLORD"),
    TENANT("ROLE_TENANT"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}