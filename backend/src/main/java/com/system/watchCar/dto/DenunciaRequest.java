package com.system.watchCar.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DenunciaRequest {

    private Long idUsuario;

    private String descricao;
    private String statusDenuncia;
    private String horaOcorrencia;
    private LocalDateTime dataHora;

    // Dados do veículo
    private String placa;
    private Integer ano;

    // Dados do tipo de veículo
    private String tipo;
    private String modelo;
    private String marca;
    private String cor;
}

