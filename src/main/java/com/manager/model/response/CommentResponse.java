package com.manager.model.response;

public record CommentResponse(
        Long commentId,
        String text,
        Long taskId,
        UserResponse author
) {
}
