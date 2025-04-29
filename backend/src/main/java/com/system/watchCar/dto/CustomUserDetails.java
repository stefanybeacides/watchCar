package com.system.watchCar.dto;

import com.system.watchCar.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Aqui você pode retornar as roles do usuário, por exemplo
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();  // Retorna a senha do usuário
    }

    @Override
    public String getUsername() {
        return user.getUsername();  // Retorna o nome de usuário
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Retorne true para indicar que a conta não expirou
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Retorne true para indicar que a conta não está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Retorne true para indicar que as credenciais não expiraram
    }

    @Override
    public boolean isEnabled() {
        return true;  // Retorne true para indicar que o usuário está habilitado
    }

    public User getUser() {
        return user;  // Pode ser útil para recuperar a entidade User
    }
}
