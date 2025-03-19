package com.system.watchCar.dto;

import com.system.watchCar.entity.Permission;
import com.system.watchCar.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailsDto {
    private Long id;
    private String username;
    private String email;
    private String roleName; // Campo para armazenar o nome da role
    private List<Permission> permissions;

    // Construtor que aceita um objeto User e uma lista de permissões
    public UserDetailsDto(User user, List<Permission> permissions) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roleName = user.getRole() != null ? user.getRole().getName() : null; // Obtém o nome da role
        this.permissions = permissions;
    }
}