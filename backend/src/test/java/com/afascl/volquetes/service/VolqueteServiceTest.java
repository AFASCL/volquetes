package com.afascl.volquetes.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VolqueteServiceTest {

    @Mock
    private VolqueteRepository volqueteRepository;

    @Mock
    private VolqueteEstadoHistorialRepository historialRepository;

    @InjectMocks
    private VolqueteService volqueteService;

    @Test
    void create_ok_returnsResponse() {
        VolqueteRequest request = new VolqueteRequest();
        request.setCodigoInterno("V-001");
        request.setCodigoExterno("QR-V001");
        request.setEstadoInicial(VolqueteEstado.DISPONIBLE);

        Volquete saved = new Volquete();
        saved.setId(1L);
        saved.setCodigoInterno("V-001");
        saved.setCodigoExterno("QR-V001");
        saved.setEstadoActual(VolqueteEstado.DISPONIBLE);

        when(volqueteRepository.existsByCodigoInterno("V-001")).thenReturn(false);
        when(volqueteRepository.existsByCodigoExterno("QR-V001")).thenReturn(false);
        when(volqueteRepository.save(any(Volquete.class))).thenReturn(saved);

        VolqueteResponse response = volqueteService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCodigoInterno()).isEqualTo("V-001");
        assertThat(response.getCodigoExterno()).isEqualTo("QR-V001");
        assertThat(response.getEstadoActual()).isEqualTo(VolqueteEstado.DISPONIBLE);
        verify(volqueteRepository).save(any(Volquete.class));
    }

    @Test
    void create_codigoInternoBlank_throws422() {
        VolqueteRequest request = new VolqueteRequest();
        request.setCodigoInterno("   ");
        request.setCodigoExterno("QR-V001");

        assertThatThrownBy(() -> volqueteService.create(request))
                .isInstanceOf(VolqueteValidationException.class)
                .hasMessageContaining("codigoInterno");
    }

    @Test
    void create_duplicateCodigoInterno_throws409() {
        VolqueteRequest request = new VolqueteRequest();
        request.setCodigoInterno("V-001");
        request.setCodigoExterno("QR-V001");
        when(volqueteRepository.existsByCodigoInterno("V-001")).thenReturn(true);

        assertThatThrownBy(() -> volqueteService.create(request))
                .isInstanceOf(VolqueteConflictException.class)
                .hasMessageContaining("codigoInterno");
    }

    @Test
    void create_duplicateCodigoExterno_throws409() {
        VolqueteRequest request = new VolqueteRequest();
        request.setCodigoInterno("V-001");
        request.setCodigoExterno("QR-V001");
        when(volqueteRepository.existsByCodigoInterno("V-001")).thenReturn(false);
        when(volqueteRepository.existsByCodigoExterno("QR-V001")).thenReturn(true);

        assertThatThrownBy(() -> volqueteService.create(request))
                .isInstanceOf(VolqueteConflictException.class)
                .hasMessageContaining("codigoExterno");
    }

    @Test
    void findById_notFound_throws404() {
        when(volqueteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> volqueteService.findById(999L))
                .isInstanceOf(VolqueteNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void parseEstadoFilter_invalid_throwsBadRequest() {
        assertThatThrownBy(() -> volqueteService.parseEstadoFilter("INVALIDO"))
                .hasMessageContaining("estado no permitido");
    }

    @Test
    void parseEstadoFilter_valid_returnsOptional() {
        assertThat(volqueteService.parseEstadoFilter("DISPONIBLE")).isEqualTo(Optional.of(VolqueteEstado.DISPONIBLE));
        assertThat(volqueteService.parseEstadoFilter("disponible")).isEqualTo(Optional.of(VolqueteEstado.DISPONIBLE));
        assertThat(volqueteService.parseEstadoFilter(null)).isEmpty();
        assertThat(volqueteService.parseEstadoFilter("")).isEmpty();
    }

    @Test
    void cambiarEstado_ok_updatesEstadoAndSavesHistorial() {
        Volquete existing = new Volquete();
        existing.setId(1L);
        existing.setCodigoInterno("V-001");
        existing.setCodigoExterno("QR-V001");
        existing.setEstadoActual(VolqueteEstado.DISPONIBLE);

        Volquete updated = new Volquete();
        updated.setId(1L);
        updated.setCodigoInterno("V-001");
        updated.setCodigoExterno("QR-V001");
        updated.setEstadoActual(VolqueteEstado.EN_CLIENTE);

        VolqueteEstadoRequest request = new VolqueteEstadoRequest();
        request.setEstado(VolqueteEstado.EN_CLIENTE);
        request.setOrigen(OrigenEstado.MANUAL);

        when(volqueteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(volqueteRepository.save(any(Volquete.class))).thenReturn(updated);
        when(historialRepository.save(any(VolqueteEstadoHistorial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VolqueteResponse response = volqueteService.cambiarEstado(1L, request);

        assertThat(response.getEstadoActual()).isEqualTo(VolqueteEstado.EN_CLIENTE);
        verify(volqueteRepository).save(any(Volquete.class));
        verify(historialRepository).save(any(VolqueteEstadoHistorial.class));
    }

    @Test
    void cambiarEstado_notFound_throws404() {
        VolqueteEstadoRequest request = new VolqueteEstadoRequest();
        request.setEstado(VolqueteEstado.EN_CLIENTE);
        when(volqueteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> volqueteService.cambiarEstado(999L, request))
                .isInstanceOf(VolqueteNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void cambiarEstado_sameEstado_registersHistorial() {
        Volquete existing = new Volquete();
        existing.setId(1L);
        existing.setCodigoInterno("V-001");
        existing.setCodigoExterno("QR-V001");
        existing.setEstadoActual(VolqueteEstado.DISPONIBLE);

        VolqueteEstadoRequest request = new VolqueteEstadoRequest();
        request.setEstado(VolqueteEstado.DISPONIBLE);
        request.setOrigen(OrigenEstado.MANUAL);

        when(volqueteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(volqueteRepository.save(any(Volquete.class))).thenReturn(existing);
        when(historialRepository.save(any(VolqueteEstadoHistorial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VolqueteResponse response = volqueteService.cambiarEstado(1L, request);

        assertThat(response.getEstadoActual()).isEqualTo(VolqueteEstado.DISPONIBLE);
        verify(historialRepository).save(any(VolqueteEstadoHistorial.class));
    }

    @Test
    void cambiarEstado_sinOrigen_registersHistorialWithNull() {
        Volquete existing = new Volquete();
        existing.setId(1L);
        existing.setCodigoInterno("V-001");
        existing.setCodigoExterno("QR-V001");
        existing.setEstadoActual(VolqueteEstado.DISPONIBLE);

        Volquete updated = new Volquete();
        updated.setId(1L);
        updated.setEstadoActual(VolqueteEstado.EN_TRANSITO);

        VolqueteEstadoRequest request = new VolqueteEstadoRequest();
        request.setEstado(VolqueteEstado.EN_TRANSITO);
        // origen es null

        when(volqueteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(volqueteRepository.save(any(Volquete.class))).thenReturn(updated);
        when(historialRepository.save(any(VolqueteEstadoHistorial.class))).thenAnswer(invocation -> {
            VolqueteEstadoHistorial h = invocation.getArgument(0);
            assertThat(h.getOrigen()).isNull();
            return h;
        });

        volqueteService.cambiarEstado(1L, request);
        verify(historialRepository).save(any(VolqueteEstadoHistorial.class));
    }

    @Test
    void exportarInventario_ok_generaCsv() {
        Volquete v1 = new Volquete();
        v1.setId(1L);
        v1.setCodigoInterno("V-001");
        v1.setCodigoExterno("QR-V001");
        v1.setEstadoActual(VolqueteEstado.DISPONIBLE);

        Volquete v2 = new Volquete();
        v2.setId(2L);
        v2.setCodigoInterno("V-002");
        v2.setCodigoExterno("QR-V002");
        v2.setEstadoActual(VolqueteEstado.EN_CLIENTE);

        when(volqueteRepository.findAll()).thenReturn(List.of(v1, v2));

        String csv = volqueteService.exportarInventario();

        assertThat(csv).contains("Código Interno,Código Externo,Estado Actual");
        assertThat(csv).contains("V-001,QR-V001,DISPONIBLE");
        assertThat(csv).contains("V-002,QR-V002,EN_CLIENTE");
        verify(volqueteRepository).findAll();
    }

    @Test
    void exportarInventario_empty_generaCsvConCabeceras() {
        when(volqueteRepository.findAll()).thenReturn(List.of());

        String csv = volqueteService.exportarInventario();

        assertThat(csv).isEqualTo("Código Interno,Código Externo,Estado Actual\n");
        verify(volqueteRepository).findAll();
    }
}
