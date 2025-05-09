package com.system.watchCar.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ArquivoDTO {

    private String nome_delegacia;
    private LocalDate data_ocorrncia_bo;
    private LocalTime hora_ocorrncia;
    private String rubrica;
    private String cidade;
    private String bairro;
    private String cep;
    private String logradouro;
    private String descr_tipo_veiculo; //tipo veiculo
    private String descr_marca_veiculo;
    private String ano_modelo;
    private String placa_veiculo;
    private String desc_cor_veiculo;
    private String descr_ocorrencia_veiculo;
}
