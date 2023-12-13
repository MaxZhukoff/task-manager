package com.manager.service;

import com.manager.entity.User;
import com.manager.mapper.UserResponseMapper;
import com.manager.model.request.AuthRequest;
import com.manager.model.response.UserResponse;
import com.manager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private final static Long USER_ID = 1L;
    private final static String RAW_PASSWORD = "password";
    private final static String ENCODED_PASSWORD = "encoded_password";
    private final static String USER_EMAIL = "test@gmail.com";
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserResponseMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    public void createUser_shouldCreateUser() {
        AuthRequest authRequest = new AuthRequest(USER_EMAIL, RAW_PASSWORD);
        User user = User.builder()
                .email(USER_EMAIL)
                .password(ENCODED_PASSWORD)
                .build();
        UserResponse expectedResponse = new UserResponse(user.getUserId(), user.getEmail());

        when(userRepository.existsByEmail(USER_EMAIL))
                .thenReturn(false);
        when(userRepository.save(user))
                .thenReturn(user);
        when(userMapper.map(user))
                .thenReturn(expectedResponse);
        when(passwordEncoder.encode(RAW_PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        UserResponse actualResponse = userService.createUser(authRequest);

        assertAll("Assert userResponse",
                () -> assertThat(actualResponse).isNotNull(),
                () -> assertThat(actualResponse.userId()).isEqualTo(expectedResponse.userId()),
                () -> assertThat(actualResponse.email()).isEqualTo(expectedResponse.email())
        );
    }

    @Test
    public void createUser_shouldThrowException_whenUserAlreadyExists() {
        AuthRequest authRequest = new AuthRequest(USER_EMAIL, RAW_PASSWORD);
        when(userRepository.existsByEmail(USER_EMAIL))
                .thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.createUser(authRequest));

        verify(userRepository, times(1)).existsByEmail(USER_EMAIL);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).map(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    public void findUserById_shouldReturnUser() {
        User user = new User(USER_ID, USER_EMAIL, ENCODED_PASSWORD);
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        User actualUser = userService.findUserById(USER_ID);

        assertAll("Assert user",
                () -> assertThat(actualUser).isNotNull(),
                () -> assertThat(actualUser.getUserId()).isEqualTo(user.getUserId()),
                () -> assertThat(actualUser.getEmail()).isEqualTo(user.getEmail())
        );
    }

    @Test
    public void findUserById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(USER_ID)).
                thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.findUserById(USER_ID));

        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    public void findUserByEmail_shouldReturnUser() {
        User user = new User(USER_ID, USER_EMAIL, ENCODED_PASSWORD);
        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(java.util.Optional.of(user));

        User actualUser = userService.findUserByEmail(USER_EMAIL);

        assertAll("Assert user",
                () -> assertThat(actualUser).isNotNull(),
                () -> assertThat(actualUser.getUserId()).isEqualTo(user.getUserId()),
                () -> assertThat(actualUser.getEmail()).isEqualTo(user.getEmail())
        );
    }

    @Test
    public void findUserByEmail_shouldReturnUser_whenUserNotFound() {
        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.findUserByEmail(USER_EMAIL));

        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    public void testLoadUserByUsername() {
        User user = new User(USER_ID, USER_EMAIL, ENCODED_PASSWORD);
        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(java.util.Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(USER_EMAIL);

        assertAll("Assert userDetails",
                () -> assertThat(userDetails).isNotNull(),
                () -> assertThat(userDetails.getUsername()).isEqualTo(user.getEmail()),
                () -> assertThat(userDetails.getPassword()).isEqualTo(user.getPassword())
        );
    }
}