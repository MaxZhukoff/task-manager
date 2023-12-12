package com.manager.mapper;

import com.manager.entity.Comment;
import com.manager.model.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentResponseMapper implements Mapper<Comment, CommentResponse> {
    private final UserResponseMapper userMapper;

    @Override
    public CommentResponse map(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getTask().getTaskId(),
                userMapper.map(comment.getAuthor())
        );
    }
}
