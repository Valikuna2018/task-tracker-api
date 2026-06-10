package com.example.tasktracker.dto.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerEmail;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}