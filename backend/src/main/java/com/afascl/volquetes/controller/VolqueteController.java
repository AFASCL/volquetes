package com.afascl.volquetes.controller;

import com.afascl.volquetes.domain.VolqueteEstado;
import com.afascl.volquetes.dto.VolqueteEstadoRequest;
import com.afascl.volquetes.dto.VolqueteRequest;
import com.afascl.volquetes.dto.VolqueteResponse;
import com.afascl.volquetes.service.VolqueteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

/**
 * CRUD volquetes, listado paginado con filtro por estado, transición de estado y export. T2 + T4 + T6 Backend.
 * Contrato: memory-bank/04-api-documentation.md
 */
@RestController
@RequestMapping("/api/volquetes")
public class VolqueteController {

    private final VolqueteService volqueteService;

    public VolqueteController(VolqueteService volqueteService) {
        this.volqueteService = volqueteService;
    }

    @GetMapping
    public ResponseEntity<Page<VolqueteResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String estado) {
        Optional<VolqueteEstado> estadoFilter = volqueteService.parseEstadoFilter(estado);
        return ResponseEntity.ok(volqueteService.findAll(pageable, estadoFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolqueteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(volqueteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VolqueteResponse> create(@Valid @RequestBody VolqueteRequest request) {
        VolqueteResponse created = volqueteService.create(request);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolqueteResponse> update(@PathVariable Long id, @Valid @RequestBody VolqueteRequest request) {
        return ResponseEntity.ok(volqueteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        volqueteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<VolqueteResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody VolqueteEstadoRequest request) {
        return ResponseEntity.ok(volqueteService.cambiarEstado(id, request));
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportInventario() {
        String csv = volqueteService.exportarInventario();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "inventario-volquetes.csv");
        return ResponseEntity.ok().headers(headers).body(csv);
    }
}
