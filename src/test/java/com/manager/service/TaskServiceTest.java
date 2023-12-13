package com.manager.service;

import com.manager.entity.Priority;
import com.manager.entity.Status;
import com.manager.entity.Task;
import com.manager.entity.User;
import com.manager.mapper.TaskResponseMapper;
import com.manager.model.TaskFilter;
import com.manager.model.request.TaskCreateEditRequest;
import com.manager.model.request.TaskExecutorEditRequest;
import com.manager.model.request.TaskStatusEditRequest;
import com.manager.model.response.TaskResponse;
import com.manager.model.response.UserResponse;
import com.manager.repository.TaskRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    private final static Long TASK_AUTHOR_ID = 1L;
    private final static String TASK_AUTHOR_EMAIL = "test@gmail.com";
    private final static String TASK_AUTHOR_PASSWORD = "123456";
    private final static Long TASK_ID = 10L;
    private final static String TASK_TITLE = "Test title";
    private final static String TASK_DESCRIPTION = "Test description";
    private final static Status DEFAULT_TASK_STATUS = Status.PENDING;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;
    @Mock
    private TaskResponseMapper taskMapper;
    @InjectMocks
    private TaskService taskService;

    @Test
    public void createTask_shouldReturnCorrectTaskResponse() {
        Priority priority = Priority.HIGH;
        TaskCreateEditRequest taskRequest = new TaskCreateEditRequest(TASK_TITLE, TASK_DESCRIPTION, priority.name());
        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        UserResponse authorUserResponse = new UserResponse(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL);
        Task task = Task.builder()
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .priority(priority)
                .author(author)
                .build();
        TaskResponse expectedResponse = new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                DEFAULT_TASK_STATUS,
                priority,
                authorUserResponse,
                null
        );

        when(userService.findUserById(TASK_AUTHOR_ID))
                .thenReturn(author);
        when(taskRepository.save(task))
                .thenReturn(task);
        when(taskMapper.map(task))
                .thenReturn(expectedResponse);

        TaskResponse actualResponse = taskService.createTask(taskRequest, TASK_AUTHOR_ID);

        assertAll("Assert taskResponse",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.taskId()).isEqualTo(expectedResponse.taskId()),
                () -> assertThat(actualResponse.title()).isEqualTo(expectedResponse.title()),
                () -> assertThat(actualResponse.description()).isEqualTo(expectedResponse.description()),
                () -> assertThat(actualResponse.priority()).isEqualTo(expectedResponse.priority()),
                () -> assertThat(actualResponse.author()).isEqualTo(authorUserResponse),
                () -> assertThat(actualResponse.status()).isEqualTo(DEFAULT_TASK_STATUS),
                () -> assertThat(actualResponse.executor()).isNull()
        );
    }

    @Test
    public void editTask_shouldReturnCorrectTaskResponse() {
        Priority oldPriority = Priority.LOW;
        Priority newPriority = Priority.HIGH;
        TaskCreateEditRequest newTaskRequest = new TaskCreateEditRequest(TASK_TITLE, TASK_DESCRIPTION, newPriority.name());
        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        UserResponse authorUserResponse = new UserResponse(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL);
        Task task = Task.builder()
                .taskId(TASK_ID)
                .title("Old title")
                .description("Old description")
                .priority(oldPriority)
                .author(author)
                .build();
        TaskResponse expectedResponse = new TaskResponse(
                task.getTaskId(),
                newTaskRequest.title(),
                newTaskRequest.description(),
                DEFAULT_TASK_STATUS,
                newPriority,
                authorUserResponse,
                null
        );

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);
        when(taskMapper.map(task))
                .thenReturn(expectedResponse);

        TaskResponse actualResponse = taskService.editTask(newTaskRequest, TASK_ID, TASK_AUTHOR_ID);

        assertAll("Assert taskResponse",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.taskId()).isEqualTo(expectedResponse.taskId()),
                () -> assertThat(actualResponse.title()).isEqualTo(expectedResponse.title()),
                () -> assertThat(actualResponse.description()).isEqualTo(expectedResponse.description()),
                () -> assertThat(actualResponse.priority()).isEqualTo(expectedResponse.priority()),
                () -> assertThat(actualResponse.author()).isEqualTo(authorUserResponse),
                () -> assertThat(actualResponse.status()).isEqualTo(DEFAULT_TASK_STATUS),
                () -> assertThat(actualResponse.executor()).isNull()
        );
    }

    @Test
    public void editTask_shouldThrowException_whenTaskNotFound() {
        Priority priority = Priority.HIGH;
        TaskCreateEditRequest request = new TaskCreateEditRequest(TASK_TITLE, TASK_DESCRIPTION, priority.name());

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> taskService.editTask(request, TASK_ID, TASK_AUTHOR_ID));

        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).map(any());
    }

    @Test
    public void editTask_shouldThrowException_whenUserIsNotAuthor() {
        Long requestUserId = 2L;
        Priority oldPriority = Priority.LOW;
        Priority newPriority = Priority.HIGH;
        TaskCreateEditRequest request = new TaskCreateEditRequest(TASK_TITLE, TASK_DESCRIPTION, newPriority.name());
        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        Task task = Task.builder()
                .taskId(TASK_ID)
                .title("Old title")
                .description("Old description")
                .priority(oldPriority)
                .author(author)
                .build();

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));

        assertThrows(ResponseStatusException.class, () -> taskService.editTask(request, TASK_ID, requestUserId));

        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).map(any());
    }

    @Test
    public void editTaskStatus_shouldReturnCorrectTaskResponse_whenEditedByAuthor() {
        Status oldStatus = Status.PENDING;
        Status newStatus = Status.IN_PROGRESS;
        TaskStatusEditRequest statusEditRequest = new TaskStatusEditRequest(newStatus.name());

        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        UserResponse authorUserResponse = new UserResponse(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL);
        Task task = Task.builder()
                .taskId(TASK_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(oldStatus)
                .priority(Priority.MEDIUM)
                .author(author)
                .executor(null)
                .build();

        TaskResponse expectedResponse = new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                newStatus,
                task.getPriority(),
                authorUserResponse,
                null
        );

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);
        when(taskMapper.map(task))
                .thenReturn(expectedResponse);

        TaskResponse actualResponse = taskService.editTaskStatus(statusEditRequest, TASK_ID, TASK_AUTHOR_ID);

        assertAll("Assert taskResponse",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.taskId()).isEqualTo(expectedResponse.taskId()),
                () -> assertThat(actualResponse.title()).isEqualTo(expectedResponse.title()),
                () -> assertThat(actualResponse.description()).isEqualTo(expectedResponse.description()),
                () -> assertThat(actualResponse.priority()).isEqualTo(expectedResponse.priority()),
                () -> assertThat(actualResponse.author()).isEqualTo(authorUserResponse),
                () -> assertThat(actualResponse.status()).isEqualTo(expectedResponse.status()),
                () -> assertThat(actualResponse.executor()).isNull()
        );
    }

    @Test
    public void editTaskStatus_shouldThrowException_whenUserHasNoPermission() {
        Long requestUserId = 2L;
        Long executorUserId = 3L;
        Status oldStatus = Status.IN_PROGRESS;
        Status newStatus = Status.COMPLETED;
        TaskStatusEditRequest statusEditRequest = new TaskStatusEditRequest(newStatus.name());

        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        User executor = new User(executorUserId, "executor@gmail.com", "1234567");
        Task task = Task.builder()
                .taskId(TASK_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(oldStatus)
                .priority(Priority.MEDIUM)
                .author(author)
                .executor(executor)
                .build();

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));

        assertThrows(ResponseStatusException.class,
                () -> taskService.editTaskStatus(statusEditRequest, TASK_ID, requestUserId));

        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).map(any());
    }

    @Test
    public void editTaskStatus_shouldReturnCorrectTaskResponse_whenEditedByExecutor() {
        Long executorId = 3L;
        String executorEmail = "executor@gmail.com";
        Status oldStatus = Status.IN_PROGRESS;
        Status newStatus = Status.COMPLETED;
        TaskStatusEditRequest statusEditRequest = new TaskStatusEditRequest(newStatus.name());

        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        UserResponse authorUserResponse = new UserResponse(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL);
        User executor = new User(executorId, executorEmail, "1234567");
        UserResponse executorUserResponse = new UserResponse(executorId, executorEmail);
        Task task = Task.builder()
                .taskId(TASK_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(oldStatus)
                .priority(Priority.MEDIUM)
                .author(author)
                .executor(executor)
                .build();

        TaskResponse expectedResponse = new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                newStatus,
                task.getPriority(),
                authorUserResponse,
                executorUserResponse
        );

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);
        when(taskMapper.map(task))
                .thenReturn(expectedResponse);

        TaskResponse actualResponse = taskService.editTaskStatus(statusEditRequest, TASK_ID, executorId);

        assertAll("Assert taskResponse",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.taskId()).isEqualTo(expectedResponse.taskId()),
                () -> assertThat(actualResponse.title()).isEqualTo(expectedResponse.title()),
                () -> assertThat(actualResponse.description()).isEqualTo(expectedResponse.description()),
                () -> assertThat(actualResponse.priority()).isEqualTo(expectedResponse.priority()),
                () -> assertThat(actualResponse.author()).isEqualTo(authorUserResponse),
                () -> assertThat(actualResponse.status()).isEqualTo(expectedResponse.status()),
                () -> assertThat(actualResponse.executor()).isEqualTo(expectedResponse.executor())
        );
    }

    @Test
    public void editTaskStatus_shouldThrowException_whenTaskNotFound() {
        String newStatus = Status.COMPLETED.name();
        TaskStatusEditRequest statusEditRequest = new TaskStatusEditRequest(newStatus);

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> taskService.editTaskStatus(statusEditRequest, TASK_ID, TASK_AUTHOR_ID));

        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).map(any());
    }

    @Test
    public void editTaskExecutor_shouldReturnCorrectTaskResponse() {
        Long executorId = 3L;
        String executorEmail = "executor@gmail.com";
        TaskExecutorEditRequest executorEditRequest = new TaskExecutorEditRequest(executorId);

        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        UserResponse authorUserResponse = new UserResponse(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL);
        User executor = new User(executorId, executorEmail, "1234567");
        UserResponse executorUserResponse = new UserResponse(executorId, executorEmail);
        Task task = Task.builder()
                .taskId(TASK_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(Status.PENDING)
                .priority(Priority.MEDIUM)
                .author(author)
                .executor(null)
                .build();

        TaskResponse expectedResponse = new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                authorUserResponse,
                executorUserResponse
        );

        when(userService.findUserById(executorId))
                .thenReturn(executor);
        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);
        when(taskMapper.map(task))
                .thenReturn(expectedResponse);

        TaskResponse actualResponse = taskService.editTaskExecutor(executorEditRequest, TASK_ID, TASK_AUTHOR_ID);

        assertAll("Assert taskResponse",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.taskId()).isEqualTo(expectedResponse.taskId()),
                () -> assertThat(actualResponse.title()).isEqualTo(expectedResponse.title()),
                () -> assertThat(actualResponse.description()).isEqualTo(expectedResponse.description()),
                () -> assertThat(actualResponse.priority()).isEqualTo(expectedResponse.priority()),
                () -> assertThat(actualResponse.author()).isEqualTo(authorUserResponse),
                () -> assertThat(actualResponse.status()).isEqualTo(expectedResponse.status()),
                () -> assertThat(actualResponse.executor()).isEqualTo(expectedResponse.executor())
        );
    }

    @Test
    public void editTaskExecutor_throwException_whenTaskNotFound() {
        Long executorId = 3L;
        TaskExecutorEditRequest executorEditRequest = new TaskExecutorEditRequest(executorId);

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> taskService.editTaskExecutor(executorEditRequest, TASK_ID, TASK_AUTHOR_ID));

        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).map(any());
    }

    @Test
    public void editTaskExecutor_throwException_whenUserHasNoPermission() {
        Long requestUserId = 2L;
        Long newExecutorId = 3L;
        TaskExecutorEditRequest executorEditRequest = new TaskExecutorEditRequest(newExecutorId);

        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        Task task = Task.builder()
                .taskId(TASK_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(Status.PENDING)
                .priority(Priority.MEDIUM)
                .author(author)
                .executor(null)
                .build();

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(task));

        assertThrows(ResponseStatusException.class,
                () -> taskService.editTaskExecutor(executorEditRequest, TASK_ID, requestUserId));

        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(userService, never()).findUserById(any());
        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).map(any());
    }

    @Test
    public void getTask_shouldReturnTask() {
        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        Task expectedTask = Task.builder()
                .taskId(TASK_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(Status.PENDING)
                .priority(Priority.MEDIUM)
                .author(author)
                .executor(null)
                .build();

        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.of(expectedTask));

        Task actualTask = taskService.getTask(TASK_ID);

        assertAll("Assert task",
                () -> assertThat(actualTask).isNotNull(),
                () -> assertThat(actualTask.getTaskId()).isEqualTo(expectedTask.getTaskId()),
                () -> assertThat(actualTask.getTitle()).isEqualTo(expectedTask.getTitle()),
                () -> assertThat(actualTask.getDescription()).isEqualTo(expectedTask.getDescription()),
                () -> assertThat(actualTask.getPriority()).isEqualTo(expectedTask.getPriority()),
                () -> assertThat(actualTask.getAuthor()).isEqualTo(expectedTask.getAuthor()),
                () -> assertThat(actualTask.getStatus()).isEqualTo(expectedTask.getStatus()),
                () -> assertThat(actualTask.getExecutor()).isEqualTo(expectedTask.getExecutor())
        );
    }

    @Test
    public void getTask_shouldThrowException_whenTaskNotFound() {
        when(taskRepository.findById(TASK_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> taskService.getTask(TASK_ID));

        verify(taskRepository, times(1)).findById(TASK_ID);
        verify(taskMapper, never()).map(any());
    }

    @Test
    public void testGetAllTasks() {
        Pageable pageable = Pageable.ofSize(10);
        Predicate predicate = Expressions.asBoolean(true).isTrue();

        User author = new User(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL, TASK_AUTHOR_PASSWORD);
        UserResponse authorUserResponse = new UserResponse(TASK_AUTHOR_ID, TASK_AUTHOR_EMAIL);
        Task task = Task.builder()
                .taskId(TASK_ID)
                .title(TASK_TITLE)
                .description(TASK_DESCRIPTION)
                .status(Status.PENDING)
                .priority(Priority.MEDIUM)
                .author(author)
                .executor(null)
                .build();
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        TaskResponse expectedResponse = new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                authorUserResponse,
                null
        );

        when(taskRepository.findAll(predicate, pageable))
                .thenReturn(taskPage);
        when(taskMapper.map(task))
                .thenReturn(expectedResponse);

        Page<TaskResponse> actualPage = taskService.getAllTasks(
                new TaskFilter(null, null, null, null),
                pageable
        );

        assertAll("Assert actualPage",
                () -> assertThat(actualPage).isNotNull(),
                () -> assertThat(actualPage.getTotalPages()).isEqualTo(1),
                () -> assertThat(actualPage.getTotalElements()).isEqualTo(1),
                () -> assertThat(actualPage.getContent().get(0).taskId()).isEqualTo(expectedResponse.taskId()),
                () -> assertThat(actualPage.getContent().get(0).title()).isEqualTo(expectedResponse.title()),
                () -> assertThat(actualPage.getContent().get(0).author()).isEqualTo(expectedResponse.author()),
                () -> assertThat(actualPage.getContent().get(0).description()).isEqualTo(expectedResponse.description()),
                () -> assertThat(actualPage.getContent().get(0).priority()).isEqualTo(expectedResponse.priority()),
                () -> assertThat(actualPage.getContent().get(0).status()).isEqualTo(expectedResponse.status()),
                () -> assertThat(actualPage.getContent().get(0).executor()).isNull()
        );
    }
}