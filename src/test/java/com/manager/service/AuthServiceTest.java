package com.manager.service;

import com.manager.entity.User;
import com.manager.model.AuthToken;
import com.manager.model.request.AuthRequest;
import com.manager.model.response.AuthResponse;
import com.manager.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private final static Long USER_ID = 1L;
    private final static String USER_EMAIL = "test@gmail.com";
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private AuthService authService;

    @Test
    public void authenticate_shouldAuthenticateUserAndReturnJwtToken() {
        AuthRequest authRequest = new AuthRequest(USER_EMAIL, "password");
        User user = new User(USER_ID, USER_EMAIL, "encodedPassword");
        AuthToken expectedAuthToken = new AuthToken(USER_ID, USER_EMAIL);
        String expectedJwtToken = "jwtToken";

        when(userService.findUserByEmail(authRequest.email()))
                .thenReturn(user);
        when(encoder.matches(authRequest.password(), user.getPassword()))
                .thenReturn(true);
        when(jwtUtil.createToken(expectedAuthToken))
                .thenReturn(expectedJwtToken);

        AuthResponse authResponse = authService.authenticate(authRequest);

        assertAll("Assert authResponse",
                () -> assertThat(authResponse).isNotNull(),
                () -> assertThat(authResponse.userId()).isEqualTo(USER_ID),
                () -> assertThat(authResponse.email()).isEqualTo(USER_EMAIL),
                () -> assertThat(authResponse.token()).isEqualTo(expectedJwtToken)
        );
    }

    @Test
    public void authenticate_shouldReturnExceptionWithInvalidPassword() {
        AuthRequest authRequest = new AuthRequest(USER_EMAIL, "invalidPassword");
        User user = new User(USER_ID, USER_EMAIL, "encodedPassword");

        when(userService.findUserByEmail(authRequest.email()))
                .thenReturn(user);
        when(encoder.matches(authRequest.password(), user.getPassword()))
                .thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> authService.authenticate(authRequest));

        verify(userService, times(1)).findUserByEmail(authRequest.email());
        verify(encoder, times(1)).matches(authRequest.password(), user.getPassword());
        verify(jwtUtil, never()).createToken(any());
    }

    @Test
    public void authenticate_shouldReturnExceptionWhenUserIsUnregistered() {
        AuthRequest authRequest = new AuthRequest("not@regist.com", "password");

        when(userService.findUserByEmail(authRequest.email()))
                .thenThrow(ResponseStatusException.class);

        assertThrows(ResponseStatusException.class, () -> authService.authenticate(authRequest));

        verify(userService, times(1)).findUserByEmail(authRequest.email());
        verify(encoder, never()).matches(any(), any());
        verify(jwtUtil, never()).createToken(any());
    }
}