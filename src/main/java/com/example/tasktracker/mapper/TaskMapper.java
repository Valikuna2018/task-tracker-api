package com.example.tasktracker.mapper;

import com.example.tasktracker.dto.task.TaskResponse;
import com.example.tasktracker.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    @Mapping(source = "assignedUser.email", target = "assignedUserEmail")
    TaskResponse toResponse(Task task);
}