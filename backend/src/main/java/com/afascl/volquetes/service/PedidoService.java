package com.afascl.volquetes.service;

import com.afascl.volquetes.domain.Cliente;
import com.afascl.volquetes.domain.ClienteNotFoundException;
import com.afascl.volquetes.domain.OrigenEstado;
import com.afascl.volquetes.domain.Pedido;
import com.afascl.volquetes.domain.PedidoConflictException;
import com.afascl.volquetes.domain.PedidoEstado;
import com.afascl.volquetes.domain.PedidoNotFoundException;
import com.afascl.volquetes.domain.PedidoValidationException;
import com.afascl.volquetes.domain.Volquete;
import com.afascl.volquetes.domain.VolqueteEstado;
import com.afascl.volquetes.domain.VolqueteNotFoundException;
import com.afascl.volquetes.dto.PedidoEstadoRequest;
import com.afascl.volquetes.dto.PedidoRequest;
import com.afascl.volquetes.dto.PedidoResponse;
import com.afascl.volquetes.dto.VolqueteEstadoRequest;
import com.afascl.volquetes.repository.CamionRepository;
import com.afascl.volquetes.repository.ChoferRepository;
import com.afascl.volquetes.repository.ClienteRepository;
import com.afascl.volquetes.repository.PedidoRepository;
import com.afascl.volquetes.repository.VolqueteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * CRUD pedidos, listado con filtros y cambio de estado. T2 + T3 Backend. Contrato: memory-bank/04-api-documentation.md
 */
@Service
public class PedidoService {

    private static final List<PedidoEstado> ESTADOS_ACTIVOS = List.of(
            PedidoEstado.NUEVO, PedidoEstado.ASIGNADO, PedidoEstado.ENTREGADO);

    /** Transiciones permitidas: desde estado -> conjuntos de estados destino. memory-bank/04. */
    private static final Map<PedidoEstado, Set<PedidoEstado>> TRANSICIONES_PERMITIDAS = Map.of(
            PedidoEstado.NUEVO, Set.of(PedidoEstado.ASIGNADO, PedidoEstado.CANCELADO),
            PedidoEstado.ASIGNADO, Set.of(PedidoEstado.ENTREGADO, PedidoEstado.CANCELADO),
            PedidoEstado.ENTREGADO, Set.of(PedidoEstado.RETIRADO),
            PedidoEstado.RETIRADO, Set.of(PedidoEstado.CERRADO),
            PedidoEstado.CERRADO, Set.of(),
            PedidoEstado.CANCELADO, Set.of()
    );

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final VolqueteRepository volqueteRepository;
    private final ChoferRepository choferRepository;
    private final CamionRepository camionRepository;
    private final VolqueteService volqueteService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         VolqueteRepository volqueteRepository,
                         ChoferRepository choferRepository,
                         CamionRepository camionRepository,
                         VolqueteService volqueteService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.volqueteRepository = volqueteRepository;
        this.choferRepository = choferRepository;
        this.camionRepository = camionRepository;
        this.volqueteService = volqueteService;
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

    /**
     * Cambiar estado del pedido. T3 Backend. Transiciones según memory-bank/04.
     * Para ASIGNADO exige choferId, camionId, fechaEntregaPrevista. En ENTREGADO/RETIRADO/CERRADO/CANCELADO actualiza estado del volquete (origen PEDIDO).
     */
    @Transactional
    public PedidoResponse cambiarEstado(Long id, PedidoEstadoRequest request) {
        Pedido entity = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));

        PedidoEstado estadoActual = entity.getEstado();
        PedidoEstado estadoDestino = request.getEstado();

        if (estadoActual == PedidoEstado.CERRADO || estadoActual == PedidoEstado.CANCELADO) {
            throw new PedidoValidationException("No se puede cambiar el estado de un pedido ya cerrado o cancelado");
        }

        Set<PedidoEstado> permitidos = TRANSICIONES_PERMITIDAS.getOrDefault(estadoActual, Set.of());
        if (!permitidos.contains(estadoDestino)) {
            throw new PedidoValidationException(
                    "Transición no permitida: desde " + estadoActual + " solo se puede pasar a " + permitidos);
        }

        if (estadoDestino == PedidoEstado.ASIGNADO) {
            if (request.getChoferId() == null) {
                throw new PedidoValidationException("Para estado ASIGNADO es obligatorio choferId");
            }
            if (request.getCamionId() == null) {
                throw new PedidoValidationException("Para estado ASIGNADO es obligatorio camionId");
            }
            if (request.getFechaEntregaPrevista() == null) {
                throw new PedidoValidationException("Para estado ASIGNADO es obligatoria fechaEntregaPrevista");
            }
            entity.setChofer(choferRepository.findById(request.getChoferId())
                    .orElseThrow(() -> new PedidoValidationException("Chofer no encontrado: " + request.getChoferId())));
            entity.setCamion(camionRepository.findById(request.getCamionId())
                    .orElseThrow(() -> new PedidoValidationException("Camión no encontrado: " + request.getCamionId())));
            entity.setFechaEntregaPrevista(request.getFechaEntregaPrevista());
        }

        entity.setEstado(estadoDestino);
        OffsetDateTime now = OffsetDateTime.now();
        if (estadoDestino == PedidoEstado.ENTREGADO) {
            entity.setFechaEntregaReal(now);
        } else if (estadoDestino == PedidoEstado.RETIRADO) {
            entity.setFechaRetiroReal(now);
        }

        // Actualizar estado del volquete e historial (origen PEDIDO) cuando el pedido sale de "activo"
        Long volqueteId = entity.getVolquete().getId();
        if (estadoDestino == PedidoEstado.ENTREGADO) {
            volqueteService.cambiarEstado(volqueteId, new VolqueteEstadoRequest(VolqueteEstado.EN_CLIENTE, OrigenEstado.PEDIDO));
        } else if (estadoDestino == PedidoEstado.RETIRADO) {
            volqueteService.cambiarEstado(volqueteId, new VolqueteEstadoRequest(VolqueteEstado.EN_TRANSITO, OrigenEstado.PEDIDO));
        } else if (estadoDestino == PedidoEstado.CERRADO || estadoDestino == PedidoEstado.CANCELADO) {
            volqueteService.cambiarEstado(volqueteId, new VolqueteEstadoRequest(VolqueteEstado.DISPONIBLE, OrigenEstado.PEDIDO));
        }

        entity = pedidoRepository.save(entity);
        return toResponseWithDetails(entity);
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
