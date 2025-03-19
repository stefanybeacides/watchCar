package com.system.watchCar.service;

import com.system.watchCar.dto.CustomUserDetails;
import com.system.watchCar.dto.RegisterRequest;
import com.system.watchCar.entity.Permission;
import com.system.watchCar.entity.Role;
import com.system.watchCar.entity.User;
import com.system.watchCar.repository.RoleRepository;
import com.system.watchCar.repository.UserRepository;
import com.system.watchCar.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    RoleRepository roleRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User  not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtTokenUtil.generateToken(user);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public String generateToken(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Criptografar a senha antes de salvar
            System.out.println("Senha fornecida: " + password);
            System.out.println("Senha armazenada no banco: " + user.getPassword());
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Gerar o token JWT com as permissões do usuário
                return jwtTokenUtil.generateToken(user);
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    public void register(RegisterRequest registerRequest) {
        // Criptografar a senha
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Verificar se a Role existe
        Role role = roleRepository.findById(registerRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + registerRequest.getRole()));

        // Criar um novo usuário
        User newUser  = new User();
        newUser .setUsername(registerRequest.getUsername());
        newUser .setPassword(encodedPassword);
        newUser .setEmail(registerRequest.getEmail());
        newUser .setRole(role); // Atribui a Role ao usuário

        // Salvar o usuário no banco de dados
        userRepository.save(newUser );
    }


    public User getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User  not found"));
    }

    public List<Permission> getUserPermissions(String username) {
        User user = getUserDetails(username);
        return user.getPermissions();
    }
}
