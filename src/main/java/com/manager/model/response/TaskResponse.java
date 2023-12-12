package com.manager.model.response;

import com.manager.entity.Priority;
import com.manager.entity.Status;

public record TaskResponse(
        Long taskId,
        String title,
        String description,
        Status status,
        Priority priority,
        UserResponse author,
        UserResponse executor
) {
}
