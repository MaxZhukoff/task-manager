package com.manager.model.response;

public record AuthResponse(
        Long userId,
        String email,
        String token
) {
}
