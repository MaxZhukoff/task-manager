package com.manager.mapper;

import com.manager.entity.Comment;
import com.manager.entity.Task;
import com.manager.model.response.CommentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentResponseMapperTest {
    @InjectMocks
    private CommentResponseMapper commentMapper;
    @Mock
    private UserResponseMapper userMapper;

    @Test
    void map_shouldMapCommentToCommentResponse() {
        Long commentId = 1L;
        String commentText = "text";
        Long taskId = 2L;
        Task task = Task.builder().taskId(taskId).build();
        Comment comment = new Comment(commentId, commentText, task, null);
        when(userMapper.map(any()))
                .thenReturn(null);

        CommentResponse commentResponse = commentMapper.map(comment);

        assertAll("Assert userResponse",
                () -> assertThat(commentResponse.commentId()).isEqualTo(comment.getCommentId()),
                () -> assertThat(commentResponse.taskId()).isEqualTo(taskId),
                () -> assertThat(commentResponse.text()).isEqualTo(commentText)
        );
    }
}