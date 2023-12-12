package com.manager.model;

public record AuthToken(
        Long userId,
        String email
) {
}
