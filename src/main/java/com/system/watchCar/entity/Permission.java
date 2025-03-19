package com.system.watchCar.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Construtor padrão
    public Permission() {
    }

    // Construtor que aceita o nome da permissão
    public Permission(String name) {
        this.name = name;
    }
}