package com.afascl.volquetes.controller;

import com.afascl.volquetes.domain.PedidoEstado;
import com.afascl.volquetes.dto.PedidoResponse;
import com.afascl.volquetes.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Test
    void post_create_returns201() throws Exception {
        String body = """
                {"clienteId":1,"volqueteId":10,"direccionEntrega":"Calle Falsa 123"}
                """;
        PedidoResponse response = new PedidoResponse();
        response.setId(1L);
        response.setClienteId(1L);
        response.setVolqueteId(10L);
        response.setDireccionEntrega("Calle Falsa 123");
        response.setEstado(PedidoEstado.NUEVO);

        when(pedidoService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.volqueteId").value(10))
                .andExpect(jsonPath("$.direccionEntrega").value("Calle Falsa 123"))
                .andExpect(jsonPath("$.estado").value("NUEVO"));
    }

    @Test
    void get_list_returns200() throws Exception {
        PedidoResponse item = new PedidoResponse();
        item.setId(1L);
        item.setEstado(PedidoEstado.NUEVO);
        when(pedidoService.findAll(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].estado").value("NUEVO"));
    }

    @Test
    void get_byId_returns200() throws Exception {
        PedidoResponse response = new PedidoResponse();
        response.setId(1L);
        response.setClienteNombre("Cliente SA");
        response.setEstado(PedidoEstado.NUEVO);
        when(pedidoService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clienteNombre").value("Cliente SA"));
    }
}
