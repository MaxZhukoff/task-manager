package com.manager.controller;

import com.manager.model.AuthToken;
import com.manager.model.request.AuthRequest;
import com.manager.model.response.ApiErrorResponse;
import com.manager.model.response.UserResponse;
import com.manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Get current authorized user",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
                    )
            })
    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal AuthToken user) {
        return new UserResponse(user.userId(), user.email());
    }

    @Operation(
            summary = "Create new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "User with this email already exists", content = @Content())
            }
    )
    @PostMapping
    @ResponseStatus(CREATED)
    public UserResponse createUser(@RequestBody @Valid AuthRequest authRequest) {
        return userService.createUser(authRequest);
    }
}
