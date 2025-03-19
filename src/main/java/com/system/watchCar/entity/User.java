package com.system.watchCar.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)  // Um usuário pode ter somente uma Role
    @JoinColumn(name = "role_id")  // Chave estrangeira que referencia a tabela "role"
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions; // Adiciona a lista de permissões


    // Método para obter as authorities do usuário para autenticação
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Atribuindo a autoridade usando o nome da role
        return List.of(new SimpleGrantedAuthority(role.getName()));  // Assuming "getName()" retrieves the role's name
    }

}
