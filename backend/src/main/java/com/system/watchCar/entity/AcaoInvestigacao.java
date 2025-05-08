package com.system.watchCar.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TB_ACAO_INVESTIGACAO")
public class AcaoInvestigacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acao_seq")
    @SequenceGenerator(name = "acao_seq", sequenceName = "acao_seq", allocationSize = 1)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DENUNCIA", nullable = false)
    private Ocorrencia denuncia;

    @Column(name = "TIPO_ACAO", nullable = false)
    private String tipoAcao;

    @Column(name = "DESCRICAO_ACAO", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String descricaoAcao;

    @Column(name = "DATA_ACAO", nullable = false)
    private LocalDateTime dataAcao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PESSOA", nullable = false)
    private User user;
}

