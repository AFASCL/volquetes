package com.afascl.volquetes.controller;

import com.afascl.volquetes.dto.ChoferSelectorItemResponse;
import com.afascl.volquetes.service.ChoferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Selector de choferes. T2 Backend. Contrato: GET /api/choferes/selector
 */
@RestController
@RequestMapping("/api/choferes")
public class ChoferController {

    private final ChoferService choferService;

    public ChoferController(ChoferService choferService) {
        this.choferService = choferService;
    }

    @GetMapping("/selector")
    public ResponseEntity<List<ChoferSelectorItemResponse>> findSelector() {
        return ResponseEntity.ok(choferService.findSelectorItems());
    }
}
