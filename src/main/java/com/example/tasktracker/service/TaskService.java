package com.example.tasktracker.service;

import com.example.tasktracker.dto.task.UpdateTaskStatusRequest;
import com.example.tasktracker.exception.BadRequestException;
import com.example.tasktracker.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.tasktracker.enums.Priority;
import com.example.tasktracker.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.example.tasktracker.dto.project.ProjectResponse;
import com.example.tasktracker.dto.task.TaskRequest;
import com.example.tasktracker.dto.task.TaskResponse;
import com.example.tasktracker.dto.user.UserResponse;
import com.example.tasktracker.mapper.TaskMapper;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public TaskResponse createTask(TaskRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("project not found"));

        User user = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = taskMapper.toEntity(request);
        task.setAssignedUser(user);
        task.setProject(project);

        Task saveTask = taskRepository.save(task);

        return taskMapper.toResponse(saveTask);
    }

    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String currentUserEmail = authentication.getName();

        if (!task.getAssignedUser().getEmail().equals(currentUserEmail)) {
            throw new BadRequestException("You can update only your assigned tasks");
        }

        task.setStatus(request.getStatus());

        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponse(updatedTask);
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        return taskMapper.toResponse(task);
    }

    public Page<TaskResponse> getTasks(TaskStatus status, Priority priority, Pageable pageable) {
        Page<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.findByStatusAndPriority(status, priority, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByPriority(priority, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        return tasks.map(p -> taskMapper.toResponse(p));
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("task not found"));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User assignedUser = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("assigneduser not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setProject(project);
        task.setAssignedUser(assignedUser);

        Task saveTask = taskRepository.save(task);

        return taskMapper.toResponse(saveTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found");
        }

        taskRepository.deleteById(id);
    }

    public Page<TaskResponse> getTasksByProject(Long projectId, int page, int size) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return taskRepository.findByProject(project, PageRequest.of(page, size))
                .map(p -> taskMapper.toResponse(p));
    }

    public Page<TaskResponse> getTasksByAssignedUser(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return taskRepository.findByAssignedUser(user, PageRequest.of(page, size))
                .map(p -> taskMapper.toResponse(p));
    }

}
