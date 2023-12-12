package com.manager.model;

import com.manager.entity.Priority;
import com.manager.entity.Status;
import com.manager.validation.ValueOfEnum;

public record TaskFilter(
        Long authorId,
        Long executorId,
        @ValueOfEnum(enumClass = Status.class, message = "Status must be any of: PENDING, IN_PROGRESS, COMPLETED")
        String status,
        @ValueOfEnum(enumClass = Priority.class, message = "Priority must be any of: LOW, MEDIUM, HIGH")
        String priority
) {
}
