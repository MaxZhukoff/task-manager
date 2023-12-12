package com.manager.controller;

import com.manager.model.request.AuthRequest;
import com.manager.model.response.ApiErrorResponse;
import com.manager.model.response.AuthResponse;
import com.manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Authorize by email and password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request", content =
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Incorrect password", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "User with this email is not registered", content = @Content())
            }
    )
    @PostMapping("/api/v1/auth")
    public AuthResponse authenticate(@RequestBody @Valid AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }
}
