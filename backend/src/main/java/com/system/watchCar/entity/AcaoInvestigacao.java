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
    @SequenceGenerator(name = "acao_seq", sequenceName = "ADS.ISEQ$$_76234", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DENUNCIA", nullable = false)
    private Ocorrencia denuncia;

    @Column(name = "TIPO_ACAO", nullable = false)
    private String tipoAcao;

    @Lob
    @Column(name = "DESCRICAO_ACAO", nullable = false)
    private String descricaoAcao;

    @Column(name = "DATA_ACAO", nullable = false)
    private LocalDateTime dataAcao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PESSOA", nullable = false)
    private User user;
}

