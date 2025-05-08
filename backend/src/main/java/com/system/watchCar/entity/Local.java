package com.system.watchCar.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_LOCAL")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "localizacao_seq")
    @javax.persistence.SequenceGenerator(name = "localizacao_seq", sequenceName = "ISEQ$$_76242", allocationSize = 1)
    private Long id;

    @Column(length = 255)
    private String logradouro;

    @Column(length = 255)
    private String bairro;

    @Column(length = 255)
    private String cidade;

    @Column(length = 255)
    private String estado;

    @Column(length = 255)
    private String cep;
}
