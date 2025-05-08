package com.system.watchCar.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OcorrenciaDTO {

    private Long id;
    private String descricaoOcorrencia;
    private String statusDenuncia;
    private String horaOcorrencia;
    private LocalDateTime dataHora;

    private String usuarioNome;
    private String usuarioEmail;

    private String veiculoPlaca;
    private String veiculoModelo;
    private String veiculoMarca;

    // Novo campo para Artigo
    private Long artigoId;
    private String artigoCodigo;
    private String artigoDescricao;

    private String logradouro;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
