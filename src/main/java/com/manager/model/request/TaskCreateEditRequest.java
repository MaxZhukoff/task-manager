package com.manager.model.request;

import com.manager.entity.Priority;
import com.manager.validation.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;

public record TaskCreateEditRequest(
        @NotBlank(message = "Title may not be blank")
        String title,
        String description,
        @ValueOfEnum(enumClass = Priority.class, message = "Priority must be any of: LOW, MEDIUM, HIGH")
        String priority
) {
}
