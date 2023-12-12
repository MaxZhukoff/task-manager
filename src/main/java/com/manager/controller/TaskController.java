package com.manager.controller;

import com.manager.model.AuthToken;
import com.manager.model.TaskFilter;
import com.manager.model.request.TaskCreateEditRequest;
import com.manager.model.request.TaskExecutorEditRequest;
import com.manager.model.request.TaskStatusEditRequest;
import com.manager.model.response.ApiErrorResponse;
import com.manager.model.response.PageResponse;
import com.manager.model.response.TaskResponse;
import com.manager.service.TaskService;
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
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content())
})
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @Operation(
            summary = "Create new task",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task created", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))
                    )
            }
    )
    @ResponseStatus(CREATED)
    @PostMapping
    public TaskResponse createTask(@RequestBody @Valid TaskCreateEditRequest taskRequest,
                                   @AuthenticationPrincipal AuthToken user) {
        return taskService.createTask(taskRequest, user.userId());
    }

    @Operation(
            summary = "Edit task",
            description = "Only the author can edit tasks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task edited", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Only the task author can edit it", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Task with this id is not found", content = @Content())
            }
    )
    @PutMapping("/{taskId}")
    public TaskResponse editTask(
            @PathVariable Long taskId,
            @RequestBody @Valid TaskCreateEditRequest taskCreateEditRequest,
            @AuthenticationPrincipal AuthToken user
    ) {
        return taskService.editTask(taskCreateEditRequest, taskId, user.userId());
    }

    @Operation(
            summary = "Edit task status",
            description = "Only the author or executor can edit the statuses of tasks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task status edited", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Only the task author or executor can edit status", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Task with this id is not found", content = @Content())
            }
    )
    @PatchMapping("/{taskId}/status")
    public TaskResponse editTaskStatus(
            @PathVariable Long taskId,
            @RequestBody @Valid TaskStatusEditRequest statusEditRequest,
            @AuthenticationPrincipal AuthToken user
    ) {
        return taskService.editTaskStatus(statusEditRequest, taskId, user.userId());
    }

    @Operation(
            summary = "Edit task executor",
            description = "Only the author can edit the task executor",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task executor edited", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Only the author can edit the task executor", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Task with this id is not found", content = @Content())
            }
    )
    @PatchMapping("/{taskId}/executor")
    public TaskResponse editTaskExecutor(
            @PathVariable Long taskId,
            @RequestBody @Valid TaskExecutorEditRequest executorEditRequest,
            @AuthenticationPrincipal AuthToken user
    ) {
        return taskService.editTaskExecutor(executorEditRequest, taskId, user.userId());
    }

    @Operation(
            summary = "Delete task",
            description = "Only the author can delete task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task deleted"),
                    @ApiResponse(responseCode = "403", description = "Only the author can delete task", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Task with this id is not found", content = @Content())
            }
    )
    @DeleteMapping("/{taskId}")
    public void removeTask(@PathVariable Long taskId, @AuthenticationPrincipal AuthToken user) {
        taskService.removeTask(taskId, user.userId());
    }

    @Operation(
            summary = "Get task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task returned", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Task with this id is not found", content = @Content())
            }
    )
    @GetMapping("/{taskId}")
    public TaskResponse findTaskById(@PathVariable Long taskId) {
        return taskService.findTaskById(taskId);
    }

    @Operation(
            summary = "Get all tasks by filter",
            description = "You can pass filter and pagination parameters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tasks returned", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))
                    )
            }
    )
    @GetMapping
    public PageResponse<TaskResponse> getAllTasks(
            @Parameter(description = "You may not use all fields") @Valid TaskFilter taskFilter,
            @Parameter(example = """
                    {"page": 0,
                     "size": 10}"""
            ) Pageable pageable
    ) {
        return PageResponse.of(taskService.getAllTasks(taskFilter, pageable));
    }
}
