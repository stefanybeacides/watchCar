package com.system.watchCar.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DenunciaRequest {

    private Long idUsuario;
    private String username;
    private String cpf;
    private String email;
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

    private String artigoLei;
    private Boolean receberAlertas;
    // Dados de localização
    private String cep;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String estado;
}

