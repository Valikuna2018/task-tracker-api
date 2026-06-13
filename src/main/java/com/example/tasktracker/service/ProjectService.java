package com.example.tasktracker.service;

import com.example.tasktracker.dto.project.ProjectRequest;
import com.example.tasktracker.dto.project.ProjectResponse;
import com.example.tasktracker.exception.BadRequestException;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.mapper.ProjectMapper;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectResponse createProject(ProjectRequest request) {
        User currentUser = getCurrentUser();

        Project project = projectMapper.toEntity(request);
        project.setOwner(currentUser);

        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }

    public List<ProjectResponse> getAllProjects() {
        User currentUser = getCurrentUser();

        if (isManager(currentUser)) {
            return projectRepository.findByOwner(currentUser)
                    .stream()
                    .map(projectMapper::toResponse)
                    .toList();
        }

        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User currentUser = getCurrentUser();
        checkProjectAccess(project, currentUser);

        return projectMapper.toResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User currentUser = getCurrentUser();
        checkProjectAccess(project, currentUser);

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toResponse(updatedProject);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User currentUser = getCurrentUser();
        checkProjectAccess(project, currentUser);

        projectRepository.delete(project);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean isAdmin(User user) {
        return user.getRole().name().equals("ADMIN");
    }

    private boolean isManager(User user) {
        return user.getRole().name().equals("MANAGER");
    }

    private boolean isProjectOwner(Project project, User user) {
        return project.getOwner().getId().equals(user.getId());
    }

    private void checkProjectAccess(Project project, User currentUser) {
        if (!isAdmin(currentUser) && !isProjectOwner(project, currentUser)) {
            throw new BadRequestException("You do not have access to this project");
        }
    }
}