package com.manager.service;

import com.manager.entity.Comment;
import com.manager.entity.Task;
import com.manager.entity.User;
import com.manager.mapper.CommentResponseMapper;
import com.manager.model.CommentFilter;
import com.manager.model.request.CommentCreateRequest;
import com.manager.model.response.CommentResponse;
import com.manager.model.response.UserResponse;
import com.manager.repository.CommentRepository;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    private final static Long TASK_ID = 1L;
    private final static Long USER_ID = 1L;
    private final static String USER_EMAIL = "test@gmail.com";
    private final static Long COMMENT_ID = 1L;
    private final static String COMMENT_TEXT = "some text";

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskService taskService;
    @Mock
    private UserService userService;
    @Mock
    private CommentResponseMapper commentMapper;
    @InjectMocks
    private CommentService commentService;

    @Test
    public void addComment_shouldReturnCommentResponse() {
        Task task = Task.builder().taskId(TASK_ID).build();
        User user = User.builder().userId(USER_ID).build();
        UserResponse userResponse = new UserResponse(USER_ID, USER_EMAIL);
        Comment comment = new Comment(COMMENT_ID, COMMENT_TEXT, task, user);
        CommentCreateRequest commentRequest = new CommentCreateRequest(COMMENT_TEXT);
        CommentResponse expectedResponse = new CommentResponse(COMMENT_ID, COMMENT_TEXT, task.getTaskId(), userResponse);

        when(taskService.getTask(TASK_ID))
                .thenReturn(task);
        when(userService.findUserById(USER_ID))
                .thenReturn(user);
        when(commentMapper.map(comment))
                .thenReturn(expectedResponse);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentResponse commentResponse = commentService.addComment(TASK_ID, commentRequest, USER_ID);

        assertAll("Assert commentResponse",
                () -> assertThat(commentResponse).isNotNull(),
                () -> assertThat(commentResponse.commentId()).isEqualTo(COMMENT_ID),
                () -> assertThat(commentResponse.text()).isEqualTo(COMMENT_TEXT),
                () -> assertThat(commentResponse.taskId()).isEqualTo(TASK_ID),
                () -> assertThat(commentResponse.author()).isEqualTo(userResponse)
        );
    }

    @Test
    public void getAllTaskComments_shouldReturnAllComments() {
        Task task = Task.builder().taskId(TASK_ID).build();
        User user = User.builder().userId(USER_ID).build();
        UserResponse userResponse = new UserResponse(USER_ID, USER_EMAIL);
        Comment comment = new Comment(COMMENT_ID, COMMENT_TEXT, task, user);
        CommentResponse expectedResponse = new CommentResponse(COMMENT_ID, COMMENT_TEXT, task.getTaskId(), userResponse);
        CommentFilter filter = new CommentFilter(null);
        Pageable pageable = Pageable.ofSize(10);
        Page<Comment> commentsPage = new PageImpl<>(List.of(comment));

        when(commentRepository.findAll(any(Predicate.class), any(Pageable.class)))
                .thenReturn(commentsPage);
        when(commentMapper.map(any()))
                .thenReturn(expectedResponse);

        Page<CommentResponse> actualPage = commentService.getAllTaskComments(TASK_ID, filter, pageable);

        assertAll("Assert actualPage",
                () -> assertThat(actualPage).isNotNull(),
                () -> assertThat(actualPage.getTotalPages()).isEqualTo(1),
                () -> assertThat(actualPage.getTotalElements()).isEqualTo(1),
                () -> assertThat(actualPage.getContent().get(0).commentId()).isEqualTo(expectedResponse.commentId()),
                () -> assertThat(actualPage.getContent().get(0).taskId()).isEqualTo(expectedResponse.taskId()),
                () -> assertThat(actualPage.getContent().get(0).text()).isEqualTo(expectedResponse.text()),
                () -> assertThat(actualPage.getContent().get(0).author()).isEqualTo(expectedResponse.author())
        );
    }
}