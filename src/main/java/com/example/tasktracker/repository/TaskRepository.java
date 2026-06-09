package com.example.tasktracker.repository;

import com.example.tasktracker.enums.Priority;
import com.example.tasktracker.enums.TaskStatus;
import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Page<Task> findByProject(Project project,
                             Pageable pageable);

    Page<Task> findByAssignedUser(User assignedUser, Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByPriority(Priority priority, Pageable pageable);

    Page<Task> findByStatusAndPriority(TaskStatus status, Priority priority, Pageable pageable);
}
