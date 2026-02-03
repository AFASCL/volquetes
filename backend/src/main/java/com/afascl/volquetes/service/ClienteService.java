package com.afascl.volquetes.service;

import com.afascl.volquetes.domain.Cliente;
import com.afascl.volquetes.domain.ClienteNotFoundException;
import com.afascl.volquetes.domain.ClienteTipo;
import com.afascl.volquetes.domain.ClienteValidationException;
import com.afascl.volquetes.dto.ClienteRequest;
import com.afascl.volquetes.dto.ClienteResponse;
import com.afascl.volquetes.dto.ClienteSelectorItemResponse;
import com.afascl.volquetes.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponse> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ClienteResponse findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        return toResponse(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteSelectorItemResponse> findSelectorItems() {
        return clienteRepository.findSelectorItems().stream()
                .map(p -> {
                    ClienteSelectorItemResponse dto = new ClienteSelectorItemResponse();
                    dto.setId(p.getId());
                    dto.setNombre(p.getNombre());
                    dto.setTipo(p.getTipo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        validateNombreNotBlank(request.getNombre());
        Cliente entity = toEntity(request);
        entity = clienteRepository.save(entity);
        return toResponse(entity);
    }

    @Transactional
    public ClienteResponse update(Long id, ClienteRequest request) {
        validateNombreNotBlank(request.getNombre());
        Cliente entity = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        mapRequestToEntity(request, entity);
        entity = clienteRepository.save(entity);
        return toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException(id);
        }
        clienteRepository.deleteById(id);
    }

    private void validateNombreNotBlank(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ClienteValidationException("nombre no puede estar vacío");
        }
    }

    private Cliente toEntity(ClienteRequest request) {
        Cliente entity = new Cliente();
        mapRequestToEntity(request, entity);
        return entity;
    }

    private void mapRequestToEntity(ClienteRequest request, Cliente entity) {
        entity.setNombre(request.getNombre().trim());
        entity.setTelefono(request.getTelefono());
        entity.setEmail(request.getEmail());
        entity.setDireccionPrincipal(request.getDireccionPrincipal());
        entity.setTipo(request.getTipo());
    }

    private ClienteResponse toResponse(Cliente entity) {
        ClienteResponse dto = new ClienteResponse();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());
        dto.setDireccionPrincipal(entity.getDireccionPrincipal());
        dto.setTipo(entity.getTipo());
        return dto;
    }
}
