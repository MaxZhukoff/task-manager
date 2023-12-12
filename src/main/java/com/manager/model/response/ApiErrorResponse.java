package com.manager.model.response;

public record ApiErrorResponse(
        String description,
        String code,
        String exceptionName,
        String exceptionMessage
) {
}
