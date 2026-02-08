package com.afascl.volquetes.service;

import com.afascl.volquetes.domain.Cliente;
import com.afascl.volquetes.domain.ClienteNotFoundException;
import com.afascl.volquetes.domain.Pedido;
import com.afascl.volquetes.domain.PedidoConflictException;
import com.afascl.volquetes.domain.PedidoEstado;
import com.afascl.volquetes.domain.PedidoNotFoundException;
import com.afascl.volquetes.domain.PedidoValidationException;
import com.afascl.volquetes.domain.Volquete;
import com.afascl.volquetes.domain.VolqueteNotFoundException;
import com.afascl.volquetes.dto.PedidoRequest;
import com.afascl.volquetes.dto.PedidoResponse;
import com.afascl.volquetes.repository.ClienteRepository;
import com.afascl.volquetes.repository.PedidoRepository;
import com.afascl.volquetes.repository.VolqueteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD pedidos y listado con filtros. T2 Backend. Contrato: memory-bank/04-api-documentation.md
 */
@Service
public class PedidoService {

    private static final List<PedidoEstado> ESTADOS_ACTIVOS = List.of(
            PedidoEstado.NUEVO, PedidoEstado.ASIGNADO, PedidoEstado.ENTREGADO);

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final VolqueteRepository volqueteRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         VolqueteRepository volqueteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.volqueteRepository = volqueteRepository;
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findAll(Pageable pageable,
                                        Optional<PedidoEstado> estado,
                                        Optional<Long> clienteId,
                                        Optional<Long> volqueteId) {
        Specification<Pedido> spec = Specification.where(null);
        if (estado.isPresent()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("estado"), estado.get()));
        }
        if (clienteId.isPresent()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("cliente").get("id"), clienteId.get()));
        }
        if (volqueteId.isPresent()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("volquete").get("id"), volqueteId.get()));
        }
        return pedidoRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PedidoResponse findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        return toResponseWithDetails(pedido);
    }

    @Transactional
    public PedidoResponse create(PedidoRequest request) {
        validateDireccionNotBlank(request.getDireccionEntrega());
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException(request.getClienteId()));
        Volquete volquete = volqueteRepository.findById(request.getVolqueteId())
                .orElseThrow(() -> new VolqueteNotFoundException(request.getVolqueteId()));
        if (pedidoRepository.existsByVolqueteIdAndEstadoIn(volquete.getId(), ESTADOS_ACTIVOS)) {
            throw new PedidoConflictException("El volquete ya está en uso en otro pedido activo");
        }
        Pedido entity = toEntity(request, cliente, volquete);
        entity.setEstado(PedidoEstado.NUEVO);
        entity = pedidoRepository.save(entity);
        return toResponse(entity);
    }

    @Transactional
    public PedidoResponse update(Long id, PedidoRequest request) {
        validateDireccionNotBlank(request.getDireccionEntrega());
        Pedido entity = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException(request.getClienteId()));
        Volquete volquete = volqueteRepository.findById(request.getVolqueteId())
                .orElseThrow(() -> new VolqueteNotFoundException(request.getVolqueteId()));
        if (pedidoRepository.existsByVolqueteIdAndEstadoInAndIdNot(volquete.getId(), ESTADOS_ACTIVOS, id)) {
            throw new PedidoConflictException("El volquete ya está en uso en otro pedido activo");
        }
        mapRequestToEntity(request, entity, cliente, volquete);
        entity = pedidoRepository.save(entity);
        return toResponse(entity);
    }

    private void validateDireccionNotBlank(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new PedidoValidationException("direccionEntrega no puede estar vacía");
        }
    }

    private Pedido toEntity(PedidoRequest request, Cliente cliente, Volquete volquete) {
        Pedido p = new Pedido();
        mapRequestToEntity(request, p, cliente, volquete);
        return p;
    }

    private void mapRequestToEntity(PedidoRequest request, Pedido entity, Cliente cliente, Volquete volquete) {
        entity.setCliente(cliente);
        entity.setVolquete(volquete);
        entity.setDireccionEntrega(request.getDireccionEntrega().trim());
        entity.setFechaEntregaPrevista(request.getFechaEntregaPrevista());
    }

    private PedidoResponse toResponse(Pedido entity) {
        PedidoResponse dto = new PedidoResponse();
        dto.setId(entity.getId());
        dto.setClienteId(entity.getCliente().getId());
        dto.setVolqueteId(entity.getVolquete().getId());
        dto.setDireccionEntrega(entity.getDireccionEntrega());
        dto.setEstado(entity.getEstado());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaEntregaPrevista(entity.getFechaEntregaPrevista());
        dto.setFechaEntregaReal(entity.getFechaEntregaReal());
        dto.setFechaRetiroReal(entity.getFechaRetiroReal());
        dto.setChoferId(entity.getChofer() != null ? entity.getChofer().getId() : null);
        dto.setCamionId(entity.getCamion() != null ? entity.getCamion().getId() : null);
        return dto;
    }

    private PedidoResponse toResponseWithDetails(Pedido entity) {
        PedidoResponse dto = toResponse(entity);
        dto.setClienteNombre(entity.getCliente().getNombre());
        dto.setVolqueteCodigoInterno(entity.getVolquete().getCodigoInterno());
        if (entity.getChofer() != null) {
            dto.setChoferNombre(entity.getChofer().getNombre());
        }
        if (entity.getCamion() != null) {
            dto.setCamionPatente(entity.getCamion().getPatente());
        }
        return dto;
    }
}
