package com.study.tasker.mapper;

import com.study.tasker.entity.Task;
import com.study.tasker.model.TaskModel;
import com.study.tasker.model.TaskModelAfterUpdate;
import com.study.tasker.security.WebAppUserDetails;
import com.study.tasker.web.model.TaskRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
@DecoratedWith(TaskMapperDelegate.class)
public interface TaskMapper {

    Task taskRequestToTask(TaskRequest request, WebAppUserDetails details);

    @Mapping(source = "taskId", target = "id")
    Task taskRequestToTask(String taskId, TaskRequest request, WebAppUserDetails details);

    TaskModel taskToTaskModel(Task task);

    TaskModelAfterUpdate taskToTaskModelAfterUpdate(Task task);


}
