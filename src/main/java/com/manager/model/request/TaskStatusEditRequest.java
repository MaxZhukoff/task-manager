package com.manager.model.request;

import com.manager.entity.Status;
import com.manager.validation.ValueOfEnum;

public record TaskStatusEditRequest(
        @ValueOfEnum(enumClass = Status.class, message = "Status must be any of: PENDING, IN_PROGRESS, COMPLETED")
        String status
) {
}
