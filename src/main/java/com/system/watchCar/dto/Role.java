package com.system.watchCar.dto;

public enum Role {
    POLICIAL(1),
    AGENTE_SEGUANCA(2),
    INVESTIGADOR(3),
    GESTOR_SEGURANCA_PUBLICA(4),
    CIDADAO_ANONIMO(5);

    private final int id;

    Role(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getRoleName() {
        return this.name();  // Retorna o nome do enum como "POLICIAL", "AGENTE_SEGUANCA", etc.
    }

    // Método para obter o enum baseado no ID
    public static Role getById(int id) {
        for (Role role : Role.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with ID " + id);
    }
}



