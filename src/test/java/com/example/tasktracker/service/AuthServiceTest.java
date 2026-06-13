package com.example.tasktracker.service;

import com.example.tasktracker.dto.auth.RegisterRequest;
import com.example.tasktracker.dto.user.UserResponse;
import com.example.tasktracker.enums.Role;
import com.example.tasktracker.exception.BadRequestException;
import com.example.tasktracker.mapper.UserMapper;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.tasktracker.dto.auth.AuthResponse;
import com.example.tasktracker.dto.auth.LoginRequest;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldCreateUser_WhenEmailIsNotRegistered() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@test.com");
        request.setPassword("123456");
        request.setRole(Role.USER);

        User user = User.builder()
                .email("user@test.com")
                .password("123456")
                .role(Role.USER)
                .build();

        User savedUser = User.builder()
                .id(1L)
                .email("user@test.com")
                .password("hashed-password")
                .role(Role.USER)
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("user@test.com")
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("123456")).thenReturn("hashed-password");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = authService.register(request);

        assertEquals(1L, result.getId());
        assertEquals("user@test.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());

        verify(userRepository).save(user);
        verify(passwordEncoder).encode("123456");
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@test.com");
        request.setPassword("123456");
        request.setRole(Role.USER);

        when(userRepository.existsByEmail("user@test.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("123456");

        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .password("hashed-password")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse result = authService.login(request);

        assertEquals("jwt-token", result.getToken());

        verify(jwtService).generateToken(user);
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong-password");

        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .password("hashed-password")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(request));

        verify(jwtService, never()).generateToken(any());
    }
}