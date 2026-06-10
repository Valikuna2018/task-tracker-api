package com.example.tasktracker.mapper;

import com.example.tasktracker.dto.project.ProjectResponse;
import com.example.tasktracker.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.email", target = "ownerEmail")
    ProjectResponse toResponse(Project project);
}