package com.example.tasktracker.service;

import com.example.tasktracker.dto.project.ProjectRequest;
import com.example.tasktracker.dto.project.ProjectResponse;
import com.example.tasktracker.enums.Role;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.mapper.ProjectMapper;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.ProjectRepository;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createProject_ShouldCreateProject_WithAuthenticatedOwner() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("manager@test.com", null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ProjectRequest request = new ProjectRequest();
        request.setName("Task Tracker");
        request.setDescription("HR assignment");

        User owner = User.builder()
                .id(1L)
                .email("manager@test.com")
                .role(Role.MANAGER)
                .build();

        Project project = Project.builder()
                .name("Task Tracker")
                .description("HR assignment")
                .build();

        Project savedProject = Project.builder()
                .id(1L)
                .name("Task Tracker")
                .description("HR assignment")
                .owner(owner)
                .build();

        ProjectResponse response = ProjectResponse.builder()
                .id(1L)
                .name("Task Tracker")
                .description("HR assignment")
                .ownerId(1L)
                .ownerEmail("manager@test.com")
                .build();

        when(userRepository.findByEmail("manager@test.com")).thenReturn(Optional.of(owner));
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(savedProject);
        when(projectMapper.toResponse(savedProject)).thenReturn(response);

        ProjectResponse result = projectService.createProject(request);

        assertEquals(1L, result.getId());
        assertEquals("Task Tracker", result.getName());
        assertEquals("manager@test.com", result.getOwnerEmail());

        verify(projectRepository).save(project);
    }

    @Test
    void getProjectById_ShouldThrowException_WhenProjectNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getProjectById(99L));

        verify(projectMapper, never()).toResponse(any());
    }
}