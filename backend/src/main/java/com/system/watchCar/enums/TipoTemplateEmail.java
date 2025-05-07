package com.system.watchCar.enums;

public enum TipoTemplateEmail {
    ACAO_INVESTIGACAO("acao-investigacao", "Atualização na Investigação"),
    NOVA_DENUNCIA("nova-denuncia", "Nova Denúncia Registrada"),
    CONTA_CRIADA("conta-criada", "Conta Criada com sucesso");

    private final String templateNome;
    private final String titulo;

    TipoTemplateEmail(String templateNome, String titulo) {
        this.templateNome = templateNome;
        this.titulo = titulo;
    }

    public String getTemplateNome() {
        return templateNome;
    }

    public String getTitulo() {
        return titulo;
    }
}

