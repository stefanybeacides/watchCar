package com.system.watchCar.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TB_RESPONSAVEL")
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ID_DENUNCIA")
    private Ocorrencia denuncia;

    @ManyToOne
    @JoinColumn(name = "ID_USER")
    private User usuario;

    private String numDistintivo;
    private String delegacia;
    private Long status;

    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

}

