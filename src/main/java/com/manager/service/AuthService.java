package com.manager.service;

import com.manager.entity.User;
import com.manager.model.AuthToken;
import com.manager.model.request.AuthRequest;
import com.manager.model.response.AuthResponse;
import com.manager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder encoder;

    public AuthResponse authenticate(AuthRequest authRequest) {
        User user = userService.findUserByEmail(authRequest.email());

        if (!encoder.matches(authRequest.password(), user.getPassword())) {
            throw new ResponseStatusException(FORBIDDEN, "Forbidden: invalid password");
        }

        String token = jwtUtil.createToken(new AuthToken(user.getUserId(), user.getEmail()));

        return new AuthResponse(user.getUserId(), user.getEmail(), token);
    }
}
