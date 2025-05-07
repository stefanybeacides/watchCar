package com.system.watchCar.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OcorrenciaDetalhadaResponse {

    private Long id;
    private String usuarioNome;
    private String veiculoPlaca;
    private String veiculoModelo;
    private String statusDenuncia;
    private Long responsavelId;
    private List<ResponsavelHistoricoDto> historicoResponsaveis;

    public OcorrenciaDetalhadaResponse() {
    }

    public OcorrenciaDetalhadaResponse(Long id, String usuarioNome, String veiculoPlaca, String veiculoModelo,
                                       String statusDenuncia, Long responsavelId,
                                       List<ResponsavelHistoricoDto> historicoResponsaveis) {
        this.id = id;
        this.usuarioNome = usuarioNome;
        this.veiculoPlaca = veiculoPlaca;
        this.veiculoModelo = veiculoModelo;
        this.statusDenuncia = statusDenuncia;
        this.responsavelId = responsavelId;
        this.historicoResponsaveis = historicoResponsaveis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public String getVeiculoPlaca() {
        return veiculoPlaca;
    }

    public void setVeiculoPlaca(String veiculoPlaca) {
        this.veiculoPlaca = veiculoPlaca;
    }

    public String getVeiculoModelo() {
        return veiculoModelo;
    }

    public void setVeiculoModelo(String veiculoModelo) {
        this.veiculoModelo = veiculoModelo;
    }

    public String getStatusDenuncia() {
        return statusDenuncia;
    }

    public void setStatusDenuncia(String statusDenuncia) {
        this.statusDenuncia = statusDenuncia;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

    public List<ResponsavelHistoricoDto> getHistoricoResponsaveis() {
        return historicoResponsaveis;
    }

    public void setHistoricoResponsaveis(List<ResponsavelHistoricoDto> historicoResponsaveis) {
        this.historicoResponsaveis = historicoResponsaveis;
    }

    public static class ResponsavelHistoricoDto {
        private String nome;
        private LocalDateTime data;
        private String distintivo;
        private String delegacia;

        public ResponsavelHistoricoDto() {
        }

        public ResponsavelHistoricoDto(String nome, LocalDateTime data, String distintivo, String delegacia) {
            this.nome = nome;
            this.data = data;
            this.distintivo = distintivo;
            this.delegacia = delegacia;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public LocalDateTime getData() {
            return data;
        }

        public void setData(LocalDateTime data) {
            this.data = data;
        }

        public String getDistintivo() {
            return distintivo;
        }

        public void setDistintivo(String distintivo) {
            this.distintivo = distintivo;
        }

        public String getDelegacia() {
            return delegacia;
        }

        public void setDelegacia(String delegacia) {
            this.delegacia = delegacia;
        }
    }

}

