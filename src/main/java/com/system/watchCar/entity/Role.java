package com.system.watchCar.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Relacionamento ManyToMany entre Role e Permission
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions", // Nome da tabela intermediária
            joinColumns = @JoinColumn(name = "role_id"), // Chave estrangeira para a tabela Role
            inverseJoinColumns = @JoinColumn(name = "permission_id") // Chave estrangeira para a tabela Permission
    )
    private Set<Permission> permissions;
}
