package com.example.tasktracker.dto.task;

import com.example.tasktracker.enums.Priority;
import com.example.tasktracker.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private LocalDate dueDate;

    private Priority priority;

    private Long projectId;

    private String projectName;

    private Long assignedUserId;

    private String assignedUserEmail;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}