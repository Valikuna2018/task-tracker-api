package com.example.tasktracker.dto.task;


import com.example.tasktracker.enums.Priority;
import com.example.tasktracker.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TaskStatus status;

    private LocalDate dueDate;

    @NotNull
    private Priority priority;

    @NotNull
    private Long projectId;

    @NotNull
    private Long assignedUserId;

}
