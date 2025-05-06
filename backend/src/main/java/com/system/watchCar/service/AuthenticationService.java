package com.system.watchCar.service;

import com.system.watchCar.dto.RegisterRequest;
import com.system.watchCar.entity.RoleType;
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
        // Codificando a senha antes de salvar
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Verificando se o usuário já existe com base no CPF
        Optional<User> user = userRepository.findByCpf(registerRequest.getCpf());
        if (user.isPresent()) {
            throw new RuntimeException("Usuário já existe");
        } else {
            // Criando um novo usuário
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(encodedPassword);
            newUser.setEmail(registerRequest.getEmail());
            newUser.setCpf(registerRequest.getCpf());

            // Determinando o tipo de usuário e a role associada
            RoleType roleType;
            switch (registerRequest.getTipo()) {
                case 1: // Cidadão
                    roleType = RoleType.PUBLICO;
                    break;
                case 2: // Policial
                    roleType = RoleType.POLICIAL;
                    break;
                case 3: // Agente de Segurança
                    roleType = RoleType.AGENTE_DE_SEGURANCA;
                    break;
                case 4: // Investigador
                    roleType = RoleType.INVESTIGADOR;
                    break;
                case 5: // Gestor de Segurança Pública
                    roleType = RoleType.GESTOR_DE_SEGURANCA_PUBLICA;
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de usuário inválido.");
            }
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new RuntimeException("Papel não encontrado: " + roleType));
            newUser.setRole(role);

            // Adicionando campos adicionais com base no tipo de usuário
            if (registerRequest.getTipo() == 2 || registerRequest.getTipo() == 3 || registerRequest.getTipo() == 4) {
                // Se o tipo de usuário for Policial, Agente de Segurança ou Investigador
                if (registerRequest.getDelegacia() == null || registerRequest.getDistintivo() == null || registerRequest.getRa() == null) {
                    throw new IllegalArgumentException("Campos adicionais (delegacia, distintivo, RA) são obrigatórios para este tipo de usuário.");
                }

                // Setando os campos específicos para policial
                newUser.setDelegate(registerRequest.getDelegacia());
                newUser.setBadge(registerRequest.getDistintivo());
                newUser.setRa(registerRequest.getRa());
            }

            if (registerRequest.getTipo() == 5) {
                // Se o tipo de usuário for Gestor de Segurança Pública
                if (registerRequest.getDepartamento() == null || registerRequest.getCargo() == null) {
                    throw new IllegalArgumentException("Campos adicionais (departamento, cargo) são obrigatórios para este tipo de usuário.");
                }

                // Setando os campos específicos para gestor
                newUser.setDepartamento(registerRequest.getDepartamento());
                newUser.setCargo(registerRequest.getCargo());
            }

            // Salvando o usuário no banco
            userRepository.save(newUser);
        }
    }



    public User getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User  not found"));
    }

}
