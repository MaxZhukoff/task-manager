package com.manager.mapper;

import com.manager.entity.User;
import com.manager.model.response.UserResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserResponseMapperTest {
    private final UserResponseMapper userMapper = new UserResponseMapper();

    @Test
    void map_shouldMapUserToUserResponse() {
        Long userId = 1L;
        String userEmail = "test@gmail.com";
        User user = new User(userId, userEmail, "123456");

        UserResponse userResponse = userMapper.map(user);

        assertAll("Assert userResponse",
                () -> assertThat(userResponse.userId()).isEqualTo(userId),
                () -> assertThat(userResponse.email()).isEqualTo(userEmail)
        );
    }
}