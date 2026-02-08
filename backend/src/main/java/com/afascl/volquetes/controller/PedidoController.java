package com.afascl.volquetes.controller;

import com.afascl.volquetes.domain.BadRequestException;
import com.afascl.volquetes.domain.PedidoEstado;
import com.afascl.volquetes.dto.PedidoEstadoRequest;
import com.afascl.volquetes.dto.PedidoRequest;
import com.afascl.volquetes.dto.PedidoResponse;
import com.afascl.volquetes.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
 * CRUD pedidos. T2 Backend. Contrato: memory-bank/04-api-documentation.md
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<Page<PedidoResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long volqueteId) {
        Optional<PedidoEstado> estadoFilter = parseEstado(estado);
        Optional<Long> cId = Optional.ofNullable(clienteId);
        Optional<Long> vId = Optional.ofNullable(volqueteId);
        return ResponseEntity.ok(pedidoService.findAll(pageable, estadoFilter, cId, vId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> create(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse created = pedidoService.create(request);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> update(@PathVariable Long id, @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.update(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody PedidoEstadoRequest request) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, request));
    }

    private static Optional<PedidoEstado> parseEstado(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(PedidoEstado.valueOf(value.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("estado inválido: " + value + ". Valores: NUEVO, ASIGNADO, ENTREGADO, RETIRADO, CERRADO, CANCELADO");
        }
    }
}
