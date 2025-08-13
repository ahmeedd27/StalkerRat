package com.ahmed.AhmedMohmoud.controllers;

import com.ahmed.AhmedMohmoud.helpers.UserRegister;
import com.ahmed.AhmedMohmoud.helpers.UserLogin;
import com.ahmed.AhmedMohmoud.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;


    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided registration details.")
    @ApiResponse(responseCode = "200", description = "User successfully registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Invalid registration data", content = @Content)
    public ResponseEntity<Map<String, String>> signUpUser(
            @Valid @RequestBody UserRegister user
    ) {
        return userService.saveUser(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in a user", description = "Authenticates a user and returns a success message or token.")
    @ApiResponse(responseCode = "200", description = "User successfully logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    public ResponseEntity<Map<String, String>> logInUser(
            @Valid @RequestBody UserLogin userLogin
    ) {
        return userService.loginUser(userLogin);
    }


}
