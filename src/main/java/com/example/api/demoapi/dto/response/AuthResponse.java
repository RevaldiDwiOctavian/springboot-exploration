package com.example.api.demoapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private int statusCode;
    private String status;
    private UserData data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserData {
        private int id;
        private String token;
        private String type;
        private String username;
    }
}

