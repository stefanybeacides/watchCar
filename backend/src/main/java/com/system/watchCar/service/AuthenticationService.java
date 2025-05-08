package com.system.watchCar.service;

import com.system.watchCar.dto.RegisterRequest;
import com.system.watchCar.entity.RoleType;
import com.system.watchCar.entity.Role;
import com.system.watchCar.entity.User;
import com.system.watchCar.enums.TipoTemplateEmail;
import com.system.watchCar.repository.RoleRepository;
import com.system.watchCar.repository.UserRepository;
import com.system.watchCar.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final EmailService emailService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, RoleRepository roleRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
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
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtTokenUtil.generateToken(user);
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    public void register(RegisterRequest registerRequest) {
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        Optional<User> existingUserOpt = userRepository.findByCpf(registerRequest.getCpf());

        User user;

        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();

            if (Boolean.TRUE.equals(user.getAtivo())) {
                throw new RuntimeException("Usuário já registrado e ativo.");
            }

            // Reativar usuário
            user.setAtivo(true);
        } else {
            user = new User();
            user.setCpf(registerRequest.getCpf());
            user.setAtivo(true); // novo usuário já vem ativo
        }

        // Dados comuns (usado em criação ou reativação)
        user.setUsername(registerRequest.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(registerRequest.getEmail());

        // Papel (role) do usuário
        RoleType roleType = switch (registerRequest.getTipo()) {
            case 1 -> RoleType.PUBLICO;
            case 2 -> RoleType.POLICIAL;
            case 3 -> RoleType.AGENTE_DE_SEGURANCA;
            case 4 -> RoleType.INVESTIGADOR;
            case 5 -> RoleType.GESTOR_DE_SEGURANCA_PUBLICA;
            default -> throw new IllegalArgumentException("Tipo de usuário inválido.");
        };

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RuntimeException("Papel não encontrado: " + roleType));
        user.setRole(role);

        // Campos adicionais obrigatórios
        if (List.of(2, 3, 4).contains(registerRequest.getTipo())) {
            if (registerRequest.getDelegacia() == null || registerRequest.getDistintivo() == null || registerRequest.getRa() == null) {
                throw new IllegalArgumentException("Delegacia, distintivo e RA são obrigatórios para este tipo de usuário.");
            }
            user.setDelegate(registerRequest.getDelegacia());
            user.setBadge(registerRequest.getDistintivo());
            user.setRa(registerRequest.getRa());
        }

        if (registerRequest.getTipo() == 5) {
            if (registerRequest.getDepartamento() == null || registerRequest.getCargo() == null) {
                throw new IllegalArgumentException("Departamento e cargo são obrigatórios para este tipo de usuário.");
            }
            user.setDepartamento(registerRequest.getDepartamento());
            user.setCargo(registerRequest.getCargo());
        }

        // Salva novo ou atualizado
        userRepository.save(user);

        // Dados do e-mail
        Map<String, Object> dados = new HashMap<>();
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nome", registerRequest.getUsername());
        usuario.put("email", registerRequest.getEmail());
        usuario.put("cpf", registerRequest.getCpf());
        usuario.put("tipo", registerRequest.getTipo());

        if (List.of(2, 3, 4).contains(registerRequest.getTipo())) {
            usuario.put("delegacia", registerRequest.getDelegacia());
            usuario.put("distintivo", registerRequest.getDistintivo());
            usuario.put("ra", registerRequest.getRa());
        }

        if (registerRequest.getTipo() == 5) {
            usuario.put("departamento", registerRequest.getDepartamento());
            usuario.put("cargo", registerRequest.getCargo());
        }

        dados.put("usuario", usuario);

        emailService.enviarEmailComTemplate(
                registerRequest.getEmail(),
                TipoTemplateEmail.CONTA_CRIADA,
                dados
        );
    }

    public User getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User  not found"));
    }

}
