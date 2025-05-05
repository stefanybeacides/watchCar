package com.system.watchCar.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "CPF is required")
    private String cpf;

    @NotNull(message = "Tipo is required")
    private Integer tipo;
}
