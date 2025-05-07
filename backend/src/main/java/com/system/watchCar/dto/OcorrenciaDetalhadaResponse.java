package com.system.watchCar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcorrenciaDetalhadaResponse {

    private Long id;
    private String usuarioNome;
    private String veiculoPlaca;
    private String veiculoModelo;
    private String statusDenuncia;
    private Long responsavelId;
    private List<ResponsavelHistoricoDto> historicoResponsaveis;
    private List<AcaoInvestigacaoDto> acoesInvestigacao; // <-- NOVO CAMPO

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponsavelHistoricoDto {
        private String nome;
        private LocalDateTime data;
        private String distintivo;
        private String delegacia;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcaoInvestigacaoDto {
        private Long id;
        private String tipoAcao;
        private String descricaoAcao;
        private LocalDateTime dataAcao;
        private Long idResponsavel;
        private String nomeResponsavel;
        private String distintivoResponsavel;
        private String delegaciaResponsavel;
    }
}
