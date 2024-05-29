package com.study.tasker.mapper;

import com.study.tasker.entity.Task;
import com.study.tasker.entity.TaskStatus;
import com.study.tasker.entity.User;
import com.study.tasker.security.WebAppUserDetails;
import com.study.tasker.service.UserService;
import com.study.tasker.web.model.TaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public abstract class TaskMapperDelegate implements TaskMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Override
    public Task taskRequestToTask(TaskRequest request, WebAppUserDetails details) {
        Task task = new Task();
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setAuthorId(details.getId());
        task.setAssigneeId(request.getAssigneeId());
        task.setObserverIds(request.getObserverIds());
        task.setStatus(request.getStatus() == null ? null : TaskStatus.valueOf(request.getStatus().toUpperCase()));

        return task;
    }

    @Override
    public Task taskRequestToTask(String taskId, TaskRequest request, WebAppUserDetails details) {
        Task task = taskRequestToTask(request, details);
        task.setId(taskId);

        return task;
    }
}
