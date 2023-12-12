package com.manager.service;

import com.manager.entity.Comment;
import com.manager.mapper.CommentResponseMapper;
import com.manager.model.CommentFilter;
import com.manager.model.request.CommentCreateRequest;
import com.manager.model.response.CommentResponse;
import com.manager.querydsl.QPredicates;
import com.manager.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.manager.entity.QComment.comment;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final CommentResponseMapper commentMapper;

    @Transactional
    public CommentResponse addComment(Long taskId, CommentCreateRequest commentRequest, Long userId) {
        Comment comment = Comment.builder()
                .task(taskService.getTask(taskId))
                .author(userService.findUserById(userId))
                .text(commentRequest.text())
                .build();

        commentRepository.save(comment);
        return commentMapper.map(comment);
    }

    public Page<CommentResponse> getAllTaskComments(Long taskId, CommentFilter filter, Pageable pageable) {
        var predicate = QPredicates.builder()
                .add(taskId, comment.task.taskId::eq)
                .add(filter.authorId(), comment.author.userId::eq)
                .build();

        return commentRepository.findAll(predicate, pageable)
                .map(commentMapper::map);
    }
}
