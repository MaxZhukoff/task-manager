package com.manager.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @Email(message = "Email is incorrect")
        String email,
        @Size(min = 6, message = "Password must be more than 6 characters")
        String password
) {
}
