package com.system.watchCar.controller;

import com.system.watchCar.dto.LoginRequest;
import com.system.watchCar.dto.RegisterRequest;
import com.system.watchCar.dto.UserDetailsDto;
import com.system.watchCar.entity.Permission;
import com.system.watchCar.entity.User;
import com.system.watchCar.response.AuthResponse;
import com.system.watchCar.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager) {
    }

    @Operation(summary = "Login to get JWT token", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Something went wrong"),
            @ApiResponse(responseCode = "422", description = "Invalid username/password supplied")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authenticationService.generateToken(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(summary = "Register a new user", description = "Create a new user and save it to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User  registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Get user details", description = "Retrieve the details of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User  details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid token")
    })
    @GetMapping("/user")
    public ResponseEntity<UserDetailsDto> getUserDetails() {
        String username = getCurrentUsername();
        if (username == null) {
            return ResponseEntity.status(403).body(null);
        }
        User user = authenticationService.getUserDetails(username);
        List<Permission> permissions = authenticationService.getUserPermissions(username);
        UserDetailsDto userDetailsDto = new UserDetailsDto(user, permissions);
        return ResponseEntity.ok(userDetailsDto);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return null;
        }
    }
}
