package com.system.watchCar.controller;

import com.system.watchCar.dto.RoleDetailsDto;
import com.system.watchCar.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "Get all roles", description = "Retrieve all roles with their permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
    })
    @GetMapping
    public ResponseEntity<List<RoleDetailsDto>> getAllRoles() {
        List<RoleDetailsDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Get role by ID", description = "Retrieve a specific role by its ID with permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoleDetailsDto> getRoleById(@PathVariable Long id) {
        RoleDetailsDto roleDetails = roleService.getRoleById(id);
        return ResponseEntity.ok(roleDetails);
    }
}