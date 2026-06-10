package com.example.tasktracker;

import com.example.tasktracker.enums.Role;
import com.example.tasktracker.mapper.UserMapper;
import com.example.tasktracker.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskTrackerApiApplication {

    public static void main(String[] args) {

        User user = User.builder()
                .id(1L)
                .email("john@gmail.com")
                .password("secret")
                .role(Role.USER)
                .build();

        SpringApplication.run(TaskTrackerApiApplication.class, args);}

}
