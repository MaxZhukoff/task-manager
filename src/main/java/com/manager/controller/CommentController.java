package com.manager.controller;

import com.manager.model.AuthToken;
import com.manager.model.CommentFilter;
import com.manager.model.request.CommentCreateRequest;
import com.manager.model.response.ApiErrorResponse;
import com.manager.model.response.CommentResponse;
import com.manager.model.response.PageResponse;
import com.manager.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@SecurityRequirement(name = "Bearer Authentication")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad request", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Task not found")
})
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(
            summary = "Add new comment",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Comment created", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class))
                    )
            }
    )
    @ResponseStatus(CREATED)
    @PostMapping
    public CommentResponse addComment(
            @PathVariable Long taskId,
            @RequestBody @Valid CommentCreateRequest commentRequest,
            @AuthenticationPrincipal AuthToken user
    ) {
        return commentService.addComment(taskId, commentRequest, user.userId());
    }

    @Operation(
            summary = "Get all task comments by filter",
            description = "You can pass filter and pagination parameters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comments returned", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))
                    )
            }
    )
    @GetMapping
    public PageResponse<CommentResponse> getAllTaskComments(
            @PathVariable Long taskId,
            @Parameter(description = "Сan pass null") CommentFilter commentFilter,
            @Parameter(example = """
                    {"page": 0,
                     "size": 10}"""
            ) Pageable pageable
    ) {
        return PageResponse.of(commentService.getAllTaskComments(taskId, commentFilter, pageable));
    }
}
