package com.system.watchCar.service;

import com.system.watchCar.dto.RoleDetailsDto;
import com.system.watchCar.entity.Role;
import com.system.watchCar.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Método para obter todas as roles com suas permissões
    @Transactional
    public List<RoleDetailsDto> getAllRoles() {
        List<Role> roles = roleRepository.findAllRolesWithPermissions();
        return roles.stream()
                .map(role -> new RoleDetailsDto(role.getId(), role.getName(), role.getPermissions()))
                .toList();
    }

    // Método para obter uma role específica por ID
    @Transactional
    public RoleDetailsDto getRoleById(Long id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            return new RoleDetailsDto(role.getId(), role.getName(), role.getPermissions());
        } else {
            throw new RuntimeException("Role not found with id: " + id);
        }
    }
}
