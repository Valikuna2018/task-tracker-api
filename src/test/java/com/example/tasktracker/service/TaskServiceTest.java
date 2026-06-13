package com.example.tasktracker.service;

import com.example.tasktracker.dto.task.TaskResponse;
import com.example.tasktracker.dto.task.UpdateTaskStatusRequest;
import com.example.tasktracker.enums.Priority;
import com.example.tasktracker.enums.Role;
import com.example.tasktracker.enums.TaskStatus;
import com.example.tasktracker.exception.BadRequestException;
import com.example.tasktracker.mapper.TaskMapper;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatus_WhenUserIsAssigned() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user@test.com", null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User assignedUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .role(Role.USER)
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("Task Tracker")
                .build();

        Task task = Task.builder()
                .id(1L)
                .title("Implement JWT")
                .description("Auth task")
                .status(TaskStatus.TODO)
                .priority(Priority.HIGH)
                .project(project)
                .assignedUser(assignedUser)
                .build();

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.DONE);

        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("Implement JWT")
                .status(TaskStatus.DONE)
                .priority(Priority.HIGH)
                .assignedUserId(1L)
                .assignedUserEmail("user@test.com")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.updateTaskStatus(1L, request);

        assertEquals(TaskStatus.DONE, result.getStatus());
        assertEquals(TaskStatus.DONE, task.getStatus());

        verify(taskRepository).save(task);
    }

    @Test
    void updateTaskStatus_ShouldThrowException_WhenUserIsNotAssigned() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("other@test.com", null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User assignedUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .role(Role.USER)
                .build();

        Task task = Task.builder()
                .id(1L)
                .title("Implement JWT")
                .status(TaskStatus.TODO)
                .assignedUser(assignedUser)
                .build();

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.DONE);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(BadRequestException.class,
                () -> taskService.updateTaskStatus(1L, request));

        verify(taskRepository, never()).save(any());
    }
}