package com.system.watchCar.dto;

import com.system.watchCar.entity.Ocorrencia;
import com.system.watchCar.entity.Responsavel;
import com.system.watchCar.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AcaoInvestigacaoDetalhadaResponse {
    private Long id;
    private String tipoAcao;
    private String descricaoAcao;
    private LocalDateTime dataAcao;

    private User responsavel;
    private Ocorrencia denuncia;
    // Getters e Setters
}

