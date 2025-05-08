package com.system.watchCar.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "TB_DENUNCIA")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ocorrencia_seq")
    @SequenceGenerator(name = "ocorrencia_seq", sequenceName = "ISEQ$_76234", allocationSize = 1)  // Ajuste o nome da sequência conforme necessário
    @Column(name = "ID")
    private Long id;



    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "DATA_HORA")
    private LocalDateTime dataHora;

    @Column(name = "ID_VEICULO")
    private Long idVeiculo;

    @Lob
    @Column(name = "DESCRICAO_OCORRENCIA")
    private String descricaoOcorrencia;

    @Column(name = "STATUS_DENUNCIA")
    private String statusDenuncia;

    @Column(name = "ID_RESPONSAVEL")
    private Long idResponsavel;

    @Column(name = "COD_ARTIGO")
    private String codArtigo;

    @Column(name = "HORA_OCORRENCIA")
    private String horaOcorrencia;

    @Column(name = "ALERTA")
    private Long alerta;

    @ManyToOne
    @JoinColumn(name = "ID_LOCAL")
    private Local idLocal;
}
