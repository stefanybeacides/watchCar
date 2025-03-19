package com.system.watchCar.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.system.watchCar.entity.Role;
import com.system.watchCar.repository.RoleRepository;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Permission is required")
    private Long role;
}
