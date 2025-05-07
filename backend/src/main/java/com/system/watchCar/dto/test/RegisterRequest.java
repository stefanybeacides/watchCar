package com.system.watchCar.dto.test;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String cpf;
    private Integer tipo;  // Tipo de usuário: 1-Cidadão, 2-Policial, 3-Agente, 4-Investigador, 5-Gestor
    private String delegacia;
    private String distintivo;
    private String ra;
    private String departamento;
    private String cargo;

}

