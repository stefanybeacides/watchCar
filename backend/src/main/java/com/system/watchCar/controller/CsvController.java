package com.system.watchCar.controller;

import com.system.watchCar.dto.OcorrenciaDTO;
import com.system.watchCar.service.CsvBase64Service;
import com.system.watchCar.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/csv")
public class CsvController {

    @Autowired
    private CsvService csvService;

    @Autowired
    private CsvBase64Service csvBase64Service;

    // Endpoint para consumir CSV via URL
    @GetMapping("/url")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<OcorrenciaDTO> consumirCsvPorUrl(@RequestParam String url) {
        try {
            return csvService.consumirCsvPorUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Endpoint para consumir CSV enviado em base64
    @PostMapping("/base64")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<OcorrenciaDTO> consumirCsvBase64(@RequestBody String base64Data) {
        try {
            return csvBase64Service.consumirCsvBase64(base64Data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

