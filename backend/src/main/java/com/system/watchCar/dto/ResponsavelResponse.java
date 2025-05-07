package com.system.watchCar.dto;

public class ResponsavelResponse {
    private boolean isResponsavel;

    // Construtor
    public ResponsavelResponse(boolean isResponsavel) {
        this.isResponsavel = isResponsavel;
    }

    // Getter
    public boolean isResponsavel() {
        return isResponsavel;
    }

    // Setter
    public void setResponsavel(boolean isResponsavel) {
        this.isResponsavel = isResponsavel;
    }

    // Métodos para facilitar a criação da resposta
    public static ResponsavelResponse criarResponsavel(boolean isResponsavel) {
        return new ResponsavelResponse(isResponsavel);
    }
}

