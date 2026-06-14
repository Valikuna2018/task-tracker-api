package com.example.tasktracker.service;

import com.example.tasktracker.dto.task.TaskRequest;
import com.example.tasktracker.dto.task.TaskResponse;
import com.example.tasktracker.dto.task.UpdateTaskStatusRequest;
import com.example.tasktracker.enums.Priority;
import com.example.tasktracker.enums.TaskStatus;
import com.example.tasktracker.exception.BadRequestException;
import com.example.tasktracker.exception.ResourceNotFoundException;
import com.example.tasktracker.mapper.TaskMapper;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.ProjectRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public TaskResponse createTask(TaskRequest request) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User assignedUser = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isAdmin(currentUser) && !isProjectOwner(project, currentUser)) {
            throw new BadRequestException("Only project manager and admin can create task");
        }

        Task task = taskMapper.toEntity(request);
        task.setAssignedUser(assignedUser);
        task.setProject(project);

        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!isAssignedUser(task, currentUser)) {
            throw new BadRequestException("Only assigned user can update task status");
        }

        task.setStatus(request.getStatus());

        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User currentUser = getCurrentUser();
        checkTaskAccess(task, currentUser);

        return taskMapper.toResponse(task);
    }

    public Page<TaskResponse> getTasks(TaskStatus status, Priority priority, Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Task> tasks;

        if (isAdmin(currentUser)) {
            if (status != null && priority != null) {
                tasks = taskRepository.findByStatusAndPriority(status, priority, pageable);
            } else if (status != null) {
                tasks = taskRepository.findByStatus(status, pageable);
            } else if (priority != null) {
                tasks = taskRepository.findByPriority(priority, pageable);
            } else {
                tasks = taskRepository.findAll(pageable);
            }
        } else if (isManager(currentUser)) {
            if (status != null && priority != null) {
                tasks = taskRepository.findByProjectOwnerAndStatusAndPriority(
                        currentUser, status, priority, pageable);
            } else if (status != null) {
                tasks = taskRepository.findByProjectOwnerAndStatus(
                        currentUser, status, pageable);
            } else if (priority != null) {
                tasks = taskRepository.findByProjectOwnerAndPriority(
                        currentUser, priority, pageable);
            } else {
                tasks = taskRepository.findByProjectOwner(currentUser, pageable);
            }
        }else {
            if (status != null && priority != null) {
                tasks = taskRepository.findByAssignedUserAndStatusAndPriority(currentUser, status, priority, pageable);
            } else if (status != null) {
                tasks = taskRepository.findByAssignedUserAndStatus(currentUser, status, pageable);
            } else if (priority != null) {
                tasks = taskRepository.findByAssignedUserAndPriority(currentUser, priority, pageable);
            } else {
                tasks = taskRepository.findByAssignedUser(currentUser, pageable);
            }
        }

        return tasks.map(t -> taskMapper.toResponse(t));
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!isAdmin(currentUser) && !isAssignedUser(task,currentUser) &&!isProjectOwner(task, currentUser)) {
            throw new BadRequestException("Only project manager and admin and assigned user can update task");
        }

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User assignedUser = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));


        if (!project.getOwner().getId().equals(getCurrentUser().getId()) && getCurrentUser().getRole().name().equals("USER")) {
            throw new BadRequestException("User can't change task's project ID");
        }

        if (!project.getOwner().getId().equals(getCurrentUser().getId()) && getCurrentUser().getRole().name().equals("MANAGER")) {
            throw new BadRequestException("Project Owner manager can't move task to someone else's project");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setProject(project);
        task.setAssignedUser(assignedUser);

        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponse(savedTask);
    }

    public void deleteTask(Long id) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!isAdmin(currentUser) && !isProjectOwner(task, currentUser) && !isAssignedUser(task,currentUser)) {
            throw new BadRequestException("Only project manager and admin can delete task");
        }

        taskRepository.delete(task);
    }

    public Page<TaskResponse> getTasksByProject(Long projectId, int page, int size) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!isAdmin(currentUser) && !isProjectOwner(project, currentUser)) {
            throw new BadRequestException("You do not have access to this project tasks");
        }

        return taskRepository.findByProject(project, PageRequest.of(page, size))
                .map(t -> taskMapper.toResponse(t));
    }

    public Page<TaskResponse> getTasksByAssignedUser(Long userId, int page, int size) {
        User currentUser = getCurrentUser();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isAdmin(currentUser) && !currentUser.getId().equals(user.getId())) {
            throw new BadRequestException("You can view only your own assigned tasks");
        }

        return taskRepository.findByAssignedUser(user, PageRequest.of(page, size))
                .map(t -> taskMapper.toResponse(t));
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

    private boolean isAssignedUser(Task task, User user) {
        return task.getAssignedUser().getId().equals(user.getId());
    }

    private boolean isProjectOwner(Task task, User user) {
        return isManager(user)
                && task.getProject().getOwner().getId().equals(user.getId());
    }

    private boolean isProjectOwner(Project project, User user) {
        return isManager(user)
                && project.getOwner().getId().equals(user.getId());
    }

    private void checkTaskAccess(Task task, User currentUser) {
        if (!isAdmin(currentUser) && !isAssignedUser(task, currentUser) && !isProjectOwner(task, currentUser)) {
            throw new BadRequestException("You do not have access to this task");
        }
    }
}