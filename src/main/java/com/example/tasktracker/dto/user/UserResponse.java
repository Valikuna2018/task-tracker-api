package com.example.tasktracker.dto.user;

import com.example.tasktracker.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private Role role;
}
