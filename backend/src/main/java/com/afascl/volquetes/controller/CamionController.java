package com.afascl.volquetes.controller;

import com.afascl.volquetes.dto.CamionSelectorItemResponse;
import com.afascl.volquetes.service.CamionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Selector de camiones. T2 Backend. Contrato: GET /api/camiones/selector
 */
@RestController
@RequestMapping("/api/camiones")
public class CamionController {

    private final CamionService camionService;

    public CamionController(CamionService camionService) {
        this.camionService = camionService;
    }

    @GetMapping("/selector")
    public ResponseEntity<List<CamionSelectorItemResponse>> findSelector() {
        return ResponseEntity.ok(camionService.findSelectorItems());
    }
}
