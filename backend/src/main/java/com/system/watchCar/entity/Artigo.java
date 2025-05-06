package com.system.watchCar.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TB_ARTIGOS_CRIMINAIS")
public class Artigo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "COD_ARTIGO")
    private String codArtigo;

    @Column(name = "DESCRICAO")
    private String descricao;
}

