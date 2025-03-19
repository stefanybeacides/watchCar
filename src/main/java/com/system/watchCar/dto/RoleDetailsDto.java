package com.system.watchCar.dto;

import com.system.watchCar.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class RoleDetailsDto {
    private Long id;
    private String name;
    private Set<Permission> permissions;
}
