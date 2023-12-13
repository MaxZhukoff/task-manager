package com.manager.mapper;

import com.manager.entity.Priority;
import com.manager.entity.Status;
import com.manager.entity.Task;
import com.manager.model.response.TaskResponse;
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
class TaskResponseMapperTest {
    @InjectMocks
    private TaskResponseMapper taskMapper;
    @Mock
    private UserResponseMapper userMapper;

    @Test
    void map_shouldMapTaskToTaskResponse() {
        Long taskId = 1L;
        String taskTitle = "title";
        String taskDescription = "description";
        Status taskStatus = Status.PENDING;
        Priority taskPriority = Priority.MEDIUM;
        Task task = new Task(taskId, taskTitle, taskDescription, taskStatus, taskPriority, null, null);
        when(userMapper.map(any()))
                .thenReturn(null);

        TaskResponse taskResponse = taskMapper.map(task);

        assertAll("Assert taskResponse",
                () -> assertThat(taskResponse.taskId()).isEqualTo(taskId),
                () -> assertThat(taskResponse.title()).isEqualTo(taskTitle),
                () -> assertThat(taskResponse.description()).isEqualTo(taskDescription),
                () -> assertThat(taskResponse.status()).isEqualTo(taskStatus),
                () -> assertThat(taskResponse.priority()).isEqualTo(taskPriority)
        );
    }
}