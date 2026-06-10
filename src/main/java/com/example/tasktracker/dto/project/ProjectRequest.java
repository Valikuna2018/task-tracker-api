package com.example.tasktracker.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequest {
    @NotBlank
    private String name;

    private String description;
}
