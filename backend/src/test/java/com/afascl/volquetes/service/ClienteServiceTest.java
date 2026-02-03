package com.afascl.volquetes.service;

import com.afascl.volquetes.domain.Cliente;
import com.afascl.volquetes.domain.ClienteTipo;
import com.afascl.volquetes.domain.ClienteNotFoundException;
import com.afascl.volquetes.domain.ClienteValidationException;
import com.afascl.volquetes.dto.ClienteRequest;
import com.afascl.volquetes.dto.ClienteResponse;
import com.afascl.volquetes.repository.ClienteRepository;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void create_ok_returnsResponse() {
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Razón Social SA");
        request.setTelefono("123");
        request.setTipo(ClienteTipo.COMUN);

        Cliente saved = new Cliente();
        saved.setId(1L);
        saved.setNombre(request.getNombre());
        saved.setTelefono(request.getTelefono());
        saved.setTipo(request.getTipo());

        when(clienteRepository.save(any(Cliente.class))).thenReturn(saved);

        ClienteResponse response = clienteService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Razón Social SA");
        assertThat(response.getTipo()).isEqualTo(ClienteTipo.COMUN);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void create_nombreBlank_throws422() {
        ClienteRequest request = new ClienteRequest();
        request.setNombre("   ");
        request.setTipo(ClienteTipo.ABONO);

        assertThatThrownBy(() -> clienteService.create(request))
                .isInstanceOf(ClienteValidationException.class)
                .hasMessageContaining("nombre no puede estar vacío");
    }

    @Test
    void findById_notFound_throws404() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.findById(999L))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining("999");
    }
}
