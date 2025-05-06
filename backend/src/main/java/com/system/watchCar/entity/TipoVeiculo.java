package com.system.watchCar.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_TIPOVEICULO")
public class TipoVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private String modelo;
    private String marca;
    private String cor;
}

