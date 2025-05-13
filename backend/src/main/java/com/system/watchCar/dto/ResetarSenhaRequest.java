package com.system.watchCar.dto;

import lombok.Data;

@Data
public class ResetarSenhaRequest {
    private String token;
    private String novaSenha;

}

