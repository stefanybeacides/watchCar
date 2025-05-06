package com.system.watchCar.controller;

import com.system.watchCar.dto.DenunciaRequest;
import com.system.watchCar.dto.LoginRequest;
import com.system.watchCar.dto.OcorrenciaDTO;
import com.system.watchCar.entity.Ocorrencia;
import com.system.watchCar.response.AuthResponse;
import com.system.watchCar.service.OcorrenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OcorrenciaController {

    @Autowired
    private final OcorrenciaService ocorrenciaService;

    @Operation(summary = "Login to get JWT token", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Something went wrong"),
            @ApiResponse(responseCode = "422", description = "Invalid username/password supplied")
    })
    @GetMapping("/listar/ocorrencias")
    public Page<OcorrenciaDTO> listarOcorrencias(
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "") String artigo,
            @RequestParam(required = false, defaultValue = "") String hora,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ocorrenciaService.obterOcorrenciasComDetalhes(status, artigo, hora, dataInicio, dataFim, page, size);
    }


    @PostMapping("/criar")
    public ResponseEntity<Ocorrencia> criarDenuncia(@RequestBody DenunciaRequest request) {
        Ocorrencia ocorrenciaCriada = ocorrenciaService.criarDenuncia(request);
        return ResponseEntity.ok(ocorrenciaCriada);
    }
}
