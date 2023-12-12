package com.manager.mapper;

import com.manager.entity.User;
import com.manager.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResponseMapper implements Mapper<User, UserResponse> {
    @Override
    public UserResponse map(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail()
        );
    }
}
