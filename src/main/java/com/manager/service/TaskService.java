package com.manager.service;

import com.manager.entity.Priority;
import com.manager.entity.Status;
import com.manager.entity.Task;
import com.manager.mapper.TaskResponseMapper;
import com.manager.model.TaskFilter;
import com.manager.model.request.TaskCreateEditRequest;
import com.manager.model.request.TaskExecutorEditRequest;
import com.manager.model.request.TaskStatusEditRequest;
import com.manager.model.response.TaskResponse;
import com.manager.querydsl.QPredicates;
import com.manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static com.manager.entity.QTask.task;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskResponseMapper taskMapper;

    @Transactional
    public TaskResponse createTask(TaskCreateEditRequest taskRequest, Long authorId) {
        Task task = Task.builder()
                .title(taskRequest.title())
                .description(taskRequest.description())
                .priority(taskRequest.priority() == null
                        ? Priority.MEDIUM
                        : Priority.valueOf(taskRequest.priority()))
                .author(userService.findUserById(authorId))
                .build();
        task = taskRepository.save(task);

        return taskMapper.map(task);
    }

    @Transactional
    public TaskResponse editTask(TaskCreateEditRequest taskRequest, Long taskId, Long userId) {
        Task task = getTask(taskId);
        checkTaskEditPermission(task, userId);

        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setPriority(taskRequest.priority() == null ? task.getPriority() : Priority.valueOf(taskRequest.priority()));
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    @Transactional
    public TaskResponse editTaskStatus(TaskStatusEditRequest statusEditRequest, Long taskId, Long userId) {
        Task task = getTask(taskId);
        checkTaskEditStatusPermission(task, userId);

        task.setStatus(Status.valueOf(statusEditRequest.status()));
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    @Transactional
    public TaskResponse editTaskExecutor(TaskExecutorEditRequest executorEditRequest, Long taskId, Long userId) {
        Task task = getTask(taskId);
        checkTaskEditPermission(task, userId);

        task.setExecutor(userService.findUserById(executorEditRequest.executorId()));
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    @Transactional
    public void removeTask(Long taskId, Long userId) {
        Task task = getTask(taskId);
        checkTaskEditPermission(task, userId);

        taskRepository.delete(task);
    }

    public TaskResponse findTaskById(Long TaskId) {
        return taskMapper.map(getTask(TaskId));
    }

    public Page<TaskResponse> getAllTasks(TaskFilter filter, Pageable pageable) {
        var predicate = QPredicates.builder()
                .add(filter.authorId(), task.author.userId::eq)
                .add(filter.executorId(), task.executor.userId::eq)
                .add(filter.status() == null ? null : Status.valueOf(filter.status()), task.status::eq)
                .add(filter.priority() == null ? null : Priority.valueOf(filter.priority()), task.priority::eq)
                .build();

        return taskRepository.findAll(predicate, pageable)
                .map(taskMapper::map);
    }

    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task with id: " + taskId + " not found"));
    }

    private void checkTaskEditPermission(Task task, Long userId) {
        if (!Objects.equals(task.getAuthor().getUserId(), userId)) {
            throw new ResponseStatusException(
                    FORBIDDEN,
                    "To edit a task with id: " + task.getTaskId() + " you must be its creator"
            );
        }
    }

    private void checkTaskEditStatusPermission(Task task, Long userId) {
        Long executorId = task.getExecutor() == null ? null : task.getExecutor().getUserId();
        if (!Objects.equals(task.getAuthor().getUserId(), userId) && !Objects.equals(executorId, userId)) {
            throw new ResponseStatusException(
                    FORBIDDEN,
                    "To edit a task status with id: " + task.getTaskId() + " you must be its creator or executor"
            );
        }
    }
}
