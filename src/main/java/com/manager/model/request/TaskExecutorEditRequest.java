package com.manager.model.request;

import jakarta.validation.constraints.NotNull;

public record TaskExecutorEditRequest(
        @NotNull
        Long executorId
) {
}
