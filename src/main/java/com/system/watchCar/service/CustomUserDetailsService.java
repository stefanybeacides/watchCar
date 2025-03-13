package com.system.watchCar.service;

import com.system.watchCar.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busque o usuário no banco de dados
        com.system.watchCar.entity.User userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Converta o UserEntity para UserDetails
        return new User(userEntity.getUsername(), userEntity.getPassword(),
                userEntity.getRoles().stream()
                        // Extrair o nome do role, supondo que o Role tenha o método getName
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                        .collect(java.util.stream.Collectors.toList()));
    }

}
