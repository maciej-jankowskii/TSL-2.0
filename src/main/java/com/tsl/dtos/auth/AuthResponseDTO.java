package com.tsl.dtos.auth;

import lombok.Getter;

@Getter
public class AuthResponseDTO {

    private final String accessToken;
    private final String tokenType = "Bearer ";

    public AuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
