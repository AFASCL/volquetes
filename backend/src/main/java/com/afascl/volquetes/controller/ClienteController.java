package com.afascl.volquetes.controller;

import com.afascl.volquetes.dto.ClienteRequest;
import com.afascl.volquetes.dto.ClienteResponse;
import com.afascl.volquetes.dto.ClienteSelectorItemResponse;
import com.afascl.volquetes.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * CRUD y selector de clientes.
 * Contrato: memory-bank/04-api-documentation.md
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(clienteService.findAll(pageable));
    }

    @GetMapping("/selector")
    public ResponseEntity<List<ClienteSelectorItemResponse>> findSelectorItems() {
        return ResponseEntity.ok(clienteService.findSelectorItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse created = clienteService.create(request);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> update(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
