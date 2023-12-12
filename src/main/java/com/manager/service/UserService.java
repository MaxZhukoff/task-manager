package com.manager.service;

import com.manager.entity.User;
import com.manager.mapper.UserResponseMapper;
import com.manager.model.request.AuthRequest;
import com.manager.model.response.UserResponse;
import com.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserResponseMapper userMapper;
    private final PasswordEncoder encoder;

    @Transactional
    public UserResponse createUser(AuthRequest authRequest) {
        if (userRepository.existsByEmail(authRequest.email())) {
            throw new ResponseStatusException(CONFLICT, "User with email: " + authRequest.email() + " already exists");
        }

        return userMapper.map(userRepository.save(User.builder()
                .email(authRequest.email())
                .password(encoder.encode(authRequest.password()))
                .build()
        ));
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User with id: " + userId + " not found"));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User with email: " + email + " not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.emptyList()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + email));
    }
}
