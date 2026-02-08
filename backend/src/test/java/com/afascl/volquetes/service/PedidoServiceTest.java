package com.afascl.volquetes.service;

import com.afascl.volquetes.domain.Camion;
import com.afascl.volquetes.domain.Chofer;
import com.afascl.volquetes.domain.Cliente;
import com.afascl.volquetes.domain.ClienteNotFoundException;
import com.afascl.volquetes.domain.Pedido;
import com.afascl.volquetes.domain.PedidoConflictException;
import com.afascl.volquetes.domain.PedidoEstado;
import com.afascl.volquetes.domain.PedidoNotFoundException;
import com.afascl.volquetes.domain.PedidoValidationException;
import com.afascl.volquetes.domain.Volquete;
import com.afascl.volquetes.domain.VolqueteNotFoundException;
import com.afascl.volquetes.dto.PedidoEstadoRequest;
import com.afascl.volquetes.dto.PedidoRequest;
import com.afascl.volquetes.dto.PedidoResponse;
import com.afascl.volquetes.repository.CamionRepository;
import com.afascl.volquetes.repository.ChoferRepository;
import com.afascl.volquetes.repository.ClienteRepository;
import com.afascl.volquetes.repository.PedidoRepository;
import com.afascl.volquetes.repository.VolqueteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private VolqueteRepository volqueteRepository;
    @Mock
    private ChoferRepository choferRepository;
    @Mock
    private CamionRepository camionRepository;
    @Mock
    private VolqueteService volqueteService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void create_ok_returnsResponse() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(1L);
        request.setVolqueteId(10L);
        request.setDireccionEntrega("Calle Falsa 123");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        Volquete volquete = new Volquete();
        volquete.setId(10L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(volqueteRepository.findById(10L)).thenReturn(Optional.of(volquete));
        when(pedidoRepository.existsByVolqueteIdAndEstadoIn(eq(10L), any())).thenReturn(false);
        when(pedidoRepository.save(any())).thenAnswer(inv -> {
            com.afascl.volquetes.domain.Pedido p = inv.getArgument(0);
            p.setId(100L);
            p.setEstado(PedidoEstado.NUEVO);
            return p;
        });

        PedidoResponse response = pedidoService.create(request);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getEstado()).isEqualTo(PedidoEstado.NUEVO);
        assertThat(response.getClienteId()).isEqualTo(1L);
        assertThat(response.getVolqueteId()).isEqualTo(10L);
        assertThat(response.getDireccionEntrega()).isEqualTo("Calle Falsa 123");
        verify(pedidoRepository).save(any());
    }

    @Test
    void create_clienteNotFound_throws404() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(999L);
        request.setVolqueteId(10L);
        request.setDireccionEntrega("Calle 1");
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.create(request))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_volqueteNotFound_throws404() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(1L);
        request.setVolqueteId(999L);
        request.setDireccionEntrega("Calle 1");
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(volqueteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.create(request))
                .isInstanceOf(VolqueteNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_volqueteYaEnPedidoActivo_throws409() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(1L);
        request.setVolqueteId(10L);
        request.setDireccionEntrega("Calle 1");
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        Volquete volquete = new Volquete();
        volquete.setId(10L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(volqueteRepository.findById(10L)).thenReturn(Optional.of(volquete));
        when(pedidoRepository.existsByVolqueteIdAndEstadoIn(10L, List.of(PedidoEstado.NUEVO, PedidoEstado.ASIGNADO, PedidoEstado.ENTREGADO))).thenReturn(true);

        assertThatThrownBy(() -> pedidoService.create(request))
                .isInstanceOf(PedidoConflictException.class)
                .hasMessageContaining("volquete ya está en uso");
    }

    @Test
    void create_direccionBlank_throws422() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(1L);
        request.setVolqueteId(10L);
        request.setDireccionEntrega("   ");

        assertThatThrownBy(() -> pedidoService.create(request))
                .isInstanceOf(PedidoValidationException.class)
                .hasMessageContaining("direccionEntrega");
    }

    @Test
    void findById_notFound_throws404() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.findById(999L))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("999");
    }

    // --- T3 cambiarEstado ---

    @Test
    void cambiarEstado_nuevoToAsignado_returnsResponse() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(PedidoEstado.NUEVO);
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Cliente");
        pedido.setCliente(cliente);
        Volquete volquete = new Volquete();
        volquete.setId(10L);
        volquete.setCodigoInterno("V01");
        pedido.setVolquete(volquete);
        Chofer chofer = new Chofer();
        chofer.setId(5L);
        Camion camion = new Camion();
        camion.setId(7L);

        PedidoEstadoRequest request = new PedidoEstadoRequest();
        request.setEstado(PedidoEstado.ASIGNADO);
        request.setChoferId(5L);
        request.setCamionId(7L);
        request.setFechaEntregaPrevista(OffsetDateTime.now());

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(choferRepository.findById(5L)).thenReturn(Optional.of(chofer));
        when(camionRepository.findById(7L)).thenReturn(Optional.of(camion));
        when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponse response = pedidoService.cambiarEstado(1L, request);

        assertThat(response.getEstado()).isEqualTo(PedidoEstado.ASIGNADO);
        verify(pedidoRepository).save(any());
    }

    @Test
    void cambiarEstado_pedidoNotFound_throws404() {
        PedidoEstadoRequest request = new PedidoEstadoRequest();
        request.setEstado(PedidoEstado.CANCELADO);
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.cambiarEstado(999L, request))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void cambiarEstado_cerrado_throws422() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(PedidoEstado.CERRADO);
        PedidoEstadoRequest request = new PedidoEstadoRequest();
        request.setEstado(PedidoEstado.ASIGNADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.cambiarEstado(1L, request))
                .isInstanceOf(PedidoValidationException.class)
                .hasMessageContaining("cerrado o cancelado");
    }

    @Test
    void cambiarEstado_transicionNoPermitida_throws422() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(PedidoEstado.NUEVO);
        PedidoEstadoRequest request = new PedidoEstadoRequest();
        request.setEstado(PedidoEstado.ENTREGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.cambiarEstado(1L, request))
                .isInstanceOf(PedidoValidationException.class)
                .hasMessageContaining("Transición no permitida");
    }

    @Test
    void cambiarEstado_asignadoSinChofer_throws422() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(PedidoEstado.NUEVO);
        PedidoEstadoRequest request = new PedidoEstadoRequest();
        request.setEstado(PedidoEstado.ASIGNADO);
        request.setCamionId(7L);
        request.setFechaEntregaPrevista(OffsetDateTime.now());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.cambiarEstado(1L, request))
                .isInstanceOf(PedidoValidationException.class)
                .hasMessageContaining("choferId");
    }
}
