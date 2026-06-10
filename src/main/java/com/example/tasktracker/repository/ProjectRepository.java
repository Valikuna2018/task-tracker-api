package com.example.tasktracker.repository;

import com.example.tasktracker.model.Project;
import com.example.tasktracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    List<Project> findByOwner(User owner);

}
