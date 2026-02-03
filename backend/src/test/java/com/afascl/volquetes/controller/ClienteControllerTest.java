package com.afascl.volquetes.controller;

import com.afascl.volquetes.domain.ClienteTipo;
import com.afascl.volquetes.dto.ClienteResponse;
import com.afascl.volquetes.dto.ClienteSelectorItemResponse;
import com.afascl.volquetes.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Test
    void post_create_returns201() throws Exception {
        String body = """
                {"nombre":"Cliente SA","telefono":"123","email":"a@b.com","direccionPrincipal":"Calle 1","tipo":"COMUN"}
                """;
        ClienteResponse response = new ClienteResponse();
        response.setId(1L);
        response.setNombre("Cliente SA");
        response.setTipo(ClienteTipo.COMUN);

        when(clienteService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Cliente SA"))
                .andExpect(jsonPath("$.tipo").value("COMUN"));
    }

    @Test
    void get_selector_returns200() throws Exception {
        ClienteSelectorItemResponse item = new ClienteSelectorItemResponse();
        item.setId(1L);
        item.setNombre("Cliente SA");
        item.setTipo(ClienteTipo.COMUN);
        when(clienteService.findSelectorItems()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/clientes/selector"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Cliente SA"))
                .andExpect(jsonPath("$[0].tipo").value("COMUN"));
    }
}
