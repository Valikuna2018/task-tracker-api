package com.example.tasktracker.repository;

import com.example.tasktracker.enums.Priority;
import com.example.tasktracker.enums.TaskStatus;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByProject(Project project, Pageable pageable);

    Page<Task> findByAssignedUser(User assignedUser, Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(Priority priority, Pageable pageable);

    Page<Task> findByStatusAndPriority(TaskStatus status, Priority priority, Pageable pageable);

    Page<Task> findByProjectOwner(User owner, Pageable pageable);

    Page<Task> findByAssignedUserAndStatus(User assignedUser, TaskStatus status, Pageable pageable);

    Page<Task> findByAssignedUserAndPriority(User assignedUser, Priority priority, Pageable pageable);

    Page<Task> findByAssignedUserAndStatusAndPriority(User assignedUser, TaskStatus status, Priority priority, Pageable pageable
    );

    Page<Task> findByProjectOwnerAndStatus(User owner, TaskStatus status, Pageable pageable);

    Page<Task> findByProjectOwnerAndPriority(User owner, Priority priority, Pageable pageable);

    Page<Task> findByProjectOwnerAndStatusAndPriority(
            User owner,
            TaskStatus status,
            Priority priority,
            Pageable pageable
    );
}