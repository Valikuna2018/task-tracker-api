package com.example.tasktracker.dto.user;

import com.example.tasktracker.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private Role role;
}
