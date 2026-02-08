package com.afascl.volquetes.service;

import com.afascl.volquetes.domain.BadRequestException;
import com.afascl.volquetes.domain.OrigenEstado;
import com.afascl.volquetes.domain.Volquete;
import com.afascl.volquetes.domain.VolqueteConflictException;
import com.afascl.volquetes.domain.VolqueteEstado;
import com.afascl.volquetes.domain.VolqueteEstadoHistorial;
import com.afascl.volquetes.domain.VolqueteNotFoundException;
import com.afascl.volquetes.domain.VolqueteValidationException;
import com.afascl.volquetes.dto.VolqueteEstadoRequest;
import com.afascl.volquetes.dto.VolqueteRequest;
import com.afascl.volquetes.dto.VolqueteResponse;
import com.afascl.volquetes.repository.VolqueteEstadoHistorialRepository;
import com.afascl.volquetes.repository.VolqueteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio CRUD volquetes y listado con filtro por estado. T2 Backend.
 * Contrato: memory-bank/04-api-documentation.md
 */
@Service
public class VolqueteService {

    private final VolqueteRepository volqueteRepository;
    private final VolqueteEstadoHistorialRepository historialRepository;

    public VolqueteService(VolqueteRepository volqueteRepository, VolqueteEstadoHistorialRepository historialRepository) {
        this.volqueteRepository = volqueteRepository;
        this.historialRepository = historialRepository;
    }

    @Transactional(readOnly = true)
    public Page<VolqueteResponse> findAll(Pageable pageable, Optional<VolqueteEstado> estadoFilter) {
        if (estadoFilter.isPresent()) {
            return volqueteRepository.findByEstadoActual(estadoFilter.get(), pageable).map(this::toResponse);
        }
        return volqueteRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public VolqueteResponse findById(Long id) {
        Volquete entity = volqueteRepository.findById(id)
                .orElseThrow(() -> new VolqueteNotFoundException(id));
        return toResponse(entity);
    }

    @Transactional
    public VolqueteResponse create(VolqueteRequest request) {
        validateCodigosNotBlank(request.getCodigoInterno(), request.getCodigoExterno());
        String ci = request.getCodigoInterno().trim();
        String ce = request.getCodigoExterno().trim();
        checkUniquenessCreate(ci, ce);
        VolqueteEstado estado = request.getEstadoInicial() != null ? request.getEstadoInicial() : VolqueteEstado.DISPONIBLE;
        Volquete entity = toEntity(ci, ce, estado);
        entity = volqueteRepository.save(entity);
        return toResponse(entity);
    }

    @Transactional
    public VolqueteResponse update(Long id, VolqueteRequest request) {
        validateCodigosNotBlank(request.getCodigoInterno(), request.getCodigoExterno());
        Volquete entity = volqueteRepository.findById(id)
                .orElseThrow(() -> new VolqueteNotFoundException(id));
        String ci = request.getCodigoInterno().trim();
        String ce = request.getCodigoExterno().trim();
        checkUniquenessUpdate(ci, ce, id);
        entity.setCodigoInterno(ci);
        entity.setCodigoExterno(ce);
        entity = volqueteRepository.save(entity);
        return toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!volqueteRepository.existsById(id)) {
            throw new VolqueteNotFoundException(id);
        }
        volqueteRepository.deleteById(id);
    }

    /**
     * Exporta el inventario completo en formato CSV. T6 Backend.
     * Obtiene todos los volquetes (sin paginación) y genera CSV con código interno, código externo, estado actual.
     */
    @Transactional(readOnly = true)
    public String exportarInventario() {
        List<Volquete> todos = volqueteRepository.findAll();
        return generarCsv(todos);
    }

    private String generarCsv(List<Volquete> volquetes) {
        StringBuilder csv = new StringBuilder();
        // Cabeceras
        csv.append("Código Interno,Código Externo,Estado Actual\n");
        // Filas
        for (Volquete v : volquetes) {
            csv.append(escapeCsv(v.getCodigoInterno()))
                    .append(",")
                    .append(escapeCsv(v.getCodigoExterno()))
                    .append(",")
                    .append(escapeCsv(v.getEstadoActual().name()))
                    .append("\n");
        }
        return csv.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        // Si contiene coma, comilla o salto de línea, envolver en comillas y escapar comillas dobles
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Cambia el estado del volquete y registra el cambio en el historial. T4 Backend.
     * Actualiza volquetes.estado_actual e inserta en volquete_estado_historial en la misma transacción.
     * Contrato: memory-bank/04-api-documentation.md
     */
    @Transactional
    public VolqueteResponse cambiarEstado(Long id, VolqueteEstadoRequest request) {
        Volquete entity = volqueteRepository.findById(id)
                .orElseThrow(() -> new VolqueteNotFoundException(id));

        VolqueteEstado estadoAnterior = entity.getEstadoActual();
        VolqueteEstado estadoNuevo = request.getEstado();

        // Validar que el estado nuevo sea válido (ya validado por Bean Validation en DTO)
        // Validar origen si viene (ya validado por Bean Validation si se agrega @Pattern o similar)
        // Por ahora, cualquier transición entre estados es válida según el alcance del ticket

        // Si el estado nuevo es igual al actual, permitirlo pero registrar en historial igualmente
        // (permite re-registrar el mismo estado con diferente origen si aplica)

        // Actualizar estado actual
        entity.setEstadoActual(estadoNuevo);
        entity = volqueteRepository.save(entity);

        // Registrar en historial
        VolqueteEstadoHistorial historial = new VolqueteEstadoHistorial();
        historial.setVolquete(entity);
        historial.setEstadoDesde(estadoAnterior);
        historial.setEstadoHasta(estadoNuevo);
        historial.setOrigen(request.getOrigen()); // puede ser null
        historialRepository.save(historial);

        return toResponse(entity);
    }

    /**
     * Parsea el string de estado para el filtro de listado. Si no es válido lanza BadRequestException (400).
     */
    public Optional<VolqueteEstado> parseEstadoFilter(String estadoParam) {
        if (estadoParam == null || estadoParam.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(VolqueteEstado.valueOf(estadoParam.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("estado no permitido: " + estadoParam);
        }
    }

    private void validateCodigosNotBlank(String codigoInterno, String codigoExterno) {
        if (codigoInterno == null || codigoInterno.isBlank()) {
            throw new VolqueteValidationException("codigoInterno no puede estar vacío");
        }
        if (codigoExterno == null || codigoExterno.isBlank()) {
            throw new VolqueteValidationException("codigoExterno no puede estar vacío");
        }
    }

    private void checkUniquenessCreate(String codigoInterno, String codigoExterno) {
        if (volqueteRepository.existsByCodigoInterno(codigoInterno)) {
            throw new VolqueteConflictException("Ya existe un volquete con codigoInterno: " + codigoInterno);
        }
        if (volqueteRepository.existsByCodigoExterno(codigoExterno)) {
            throw new VolqueteConflictException("Ya existe un volquete con codigoExterno: " + codigoExterno);
        }
    }

    private void checkUniquenessUpdate(String codigoInterno, String codigoExterno, Long id) {
        if (volqueteRepository.existsByCodigoInternoAndIdNot(codigoInterno, id)) {
            throw new VolqueteConflictException("Ya existe otro volquete con codigoInterno: " + codigoInterno);
        }
        if (volqueteRepository.existsByCodigoExternoAndIdNot(codigoExterno, id)) {
            throw new VolqueteConflictException("Ya existe otro volquete con codigoExterno: " + codigoExterno);
        }
    }

    private Volquete toEntity(String codigoInterno, String codigoExterno, VolqueteEstado estado) {
        Volquete e = new Volquete();
        e.setCodigoInterno(codigoInterno);
        e.setCodigoExterno(codigoExterno);
        e.setEstadoActual(estado);
        return e;
    }

    private VolqueteResponse toResponse(Volquete entity) {
        VolqueteResponse dto = new VolqueteResponse();
        dto.setId(entity.getId());
        dto.setCodigoInterno(entity.getCodigoInterno());
        dto.setCodigoExterno(entity.getCodigoExterno());
        dto.setEstadoActual(entity.getEstadoActual());
        return dto;
    }
}
