package com.system.watchCar.controller;
import com.system.watchCar.dto.test.RegisterRequest;
import com.system.watchCar.enums.TipoTemplateEmail;
import com.system.watchCar.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    public String sendTestEmail(@RequestBody RegisterRequest registerRequest) {
        try {
            // Criando o mapa de dados para enviar no email
            Map<String, Object> dados = new HashMap<>();
            Map<String, Object> usuario = new HashMap<>();

            // Informações gerais do usuário
            usuario.put("nome", registerRequest.getUsername());
            usuario.put("email", registerRequest.getEmail());
            usuario.put("cpf", registerRequest.getCpf());
            usuario.put("tipo", registerRequest.getTipo());

            // Adicionando as informações adicionais baseadas no tipo de usuário
            if (registerRequest.getTipo() == 2 || registerRequest.getTipo() == 3 || registerRequest.getTipo() == 4) {
                usuario.put("delegacia", registerRequest.getDelegacia());
                usuario.put("distintivo", registerRequest.getDistintivo());
                usuario.put("ra", registerRequest.getRa());
            }

            if (registerRequest.getTipo() == 5) {
                usuario.put("departamento", registerRequest.getDepartamento());
                usuario.put("cargo", registerRequest.getCargo());
            }

            dados.put("usuario", usuario);

            // Enviar email com os dados do usuário
            emailService.enviarEmailComTemplate(
                    registerRequest.getEmail(),  // E-mail de destino
                    TipoTemplateEmail.CONTA_CRIADA,  // Nome do template
                    dados
            );

            return "E-mail de teste enviado com sucesso!";
        } catch (Exception e) {
            return "Erro ao enviar e-mail: " + e.getMessage();
        }
    }
}

