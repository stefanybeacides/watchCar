package com.system.watchCar.controller;

import com.system.watchCar.service.ArquivoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/arquivos")
public class ArquivoController {

    private final ArquivoService arquivoService;

    public ArquivoController(ArquivoService arquivoService) {
        this.arquivoService = arquivoService;
    }

    @PostMapping("/import")
    public String importarCsv(@RequestParam("file") MultipartFile file) {
        try {
            return arquivoService.importarXlsx(file);
        } catch (Exception e) {
            return String.valueOf(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao importar: " + e.getMessage()));
        }
    }

}
