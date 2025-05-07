package com.system.watchCar.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AcaoInvestigacaoRequest {
    private Long idDenuncia;
    private String tipoAcao;
    private String descricaoAcao;
    private LocalDateTime dataAcao;
    private Long idResponsavel;
}

