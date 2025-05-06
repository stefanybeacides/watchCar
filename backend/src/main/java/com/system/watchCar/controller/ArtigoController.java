package com.system.watchCar.controller;

import com.system.watchCar.dto.OcorrenciaDTO;
import com.system.watchCar.entity.Artigo;
import com.system.watchCar.service.ArtigoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/artigos")
public class ArtigoController {

    private final ArtigoService artigoService;

    public ArtigoController(ArtigoService artigoService) {
        this.artigoService = artigoService;
    }
    @Operation(summary = "Login to get JWT token", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Something went wrong"),
            @ApiResponse(responseCode = "422", description = "Invalid username/password supplied")
    })
    @GetMapping
    public List<Artigo> listarArtigos() {
        return artigoService.listarTodos();
    }
}
