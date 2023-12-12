package com.manager.mapper;

import com.manager.entity.Task;
import com.manager.model.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskResponseMapper implements Mapper<Task, TaskResponse> {
    private final UserResponseMapper userMapper;

    @Override
    public TaskResponse map(Task task) {
        return new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                userMapper.map(task.getAuthor()),
                userMapper.map(task.getExecutor())
        );
    }
}
