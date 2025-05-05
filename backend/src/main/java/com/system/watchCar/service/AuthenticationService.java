package com.system.watchCar.service;

import com.system.watchCar.dto.RegisterRequest;
import com.system.watchCar.dto.RoleType;
import com.system.watchCar.entity.Role;
import com.system.watchCar.entity.User;
import com.system.watchCar.repository.RoleRepository;
import com.system.watchCar.repository.UserRepository;
import com.system.watchCar.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final RoleRepository roleRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.roleRepository = roleRepository;
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

    public String generateToken(String cpf, String password) {
        Optional<User> userOpt = userRepository.findByCpf(cpf);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Senha fornecida: " + password);
            System.out.println("Senha armazenada no banco: " + user.getPassword());
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtTokenUtil.generateToken(user);
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    public void register(RegisterRequest registerRequest) {
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        Optional<User> user = userRepository.findByCpf(registerRequest.getCpf());
        if(user.isPresent()){
            throw new RuntimeException("Usuario ja existe");
        }else {
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(encodedPassword);
            newUser.setEmail(registerRequest.getEmail());
            newUser.setCpf(registerRequest.getCpf());

            RoleType roleType;
            switch (registerRequest.getTipo()) {
                case 1 -> roleType = RoleType.ADMIN;
                case 2 -> roleType = RoleType.POLICIAL;
                case 3 -> roleType = RoleType.PUBLICO;
                default -> throw new IllegalArgumentException("Tipo de usuário inválido.");
            }
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new RuntimeException("Papel não encontrado: " + roleType));

            newUser.setRole(role);
            userRepository.save(newUser);
        }
    }


    public User getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User  not found"));
    }

}
