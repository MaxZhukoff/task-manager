package com.manager.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank
        @Size(max = 255)
        String text
) {
}
