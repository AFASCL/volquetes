package com.afascl.volquetes.controller;

import com.afascl.volquetes.controller.advice.GlobalExceptionHandler;
import com.afascl.volquetes.domain.VolqueteEstado;
import com.afascl.volquetes.domain.VolqueteNotFoundException;
import com.afascl.volquetes.dto.VolqueteEstadoRequest;
import com.afascl.volquetes.dto.VolqueteResponse;
import com.afascl.volquetes.service.VolqueteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VolqueteController.class)
@Import(GlobalExceptionHandler.class)
class VolqueteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolqueteService volqueteService;

    @Test
    void get_list_returns200() throws Exception {
        VolqueteResponse item = new VolqueteResponse();
        item.setId(1L);
        item.setCodigoInterno("V-001");
        item.setCodigoExterno("QR-V001");
        item.setEstadoActual(VolqueteEstado.DISPONIBLE);
        Page<VolqueteResponse> page = new PageImpl<>(List.of(item));

        when(volqueteService.parseEstadoFilter(any())).thenReturn(java.util.Optional.empty());
        when(volqueteService.findAll(any(org.springframework.data.domain.Pageable.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/volquetes").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].codigoInterno").value("V-001"))
                .andExpect(jsonPath("$.content[0].estadoActual").value("DISPONIBLE"));
    }

    @Test
    void get_byId_returns200() throws Exception {
        VolqueteResponse response = new VolqueteResponse();
        response.setId(1L);
        response.setCodigoInterno("V-001");
        response.setCodigoExterno("QR-V001");
        response.setEstadoActual(VolqueteEstado.DISPONIBLE);
        when(volqueteService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/volquetes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigoInterno").value("V-001"));
    }

    @Test
    void post_create_returns201() throws Exception {
        String body = """
                {"codigoInterno":"V-001","codigoExterno":"QR-V001","estadoInicial":"DISPONIBLE"}
                """;
        VolqueteResponse created = new VolqueteResponse();
        created.setId(1L);
        created.setCodigoInterno("V-001");
        created.setCodigoExterno("QR-V001");
        created.setEstadoActual(VolqueteEstado.DISPONIBLE);
        when(volqueteService.create(any())).thenReturn(created);

        mockMvc.perform(post("/api/volquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigoInterno").value("V-001"))
                .andExpect(jsonPath("$.estadoActual").value("DISPONIBLE"));
    }

    @Test
    void put_update_returns200() throws Exception {
        String body = """
                {"codigoInterno":"V-001","codigoExterno":"QR-V002"}
                """;
        VolqueteResponse updated = new VolqueteResponse();
        updated.setId(1L);
        updated.setCodigoInterno("V-001");
        updated.setCodigoExterno("QR-V002");
        updated.setEstadoActual(VolqueteEstado.DISPONIBLE);
        when(volqueteService.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/volquetes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoExterno").value("QR-V002"));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/volquetes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void get_byId_notFound_returns404() throws Exception {
        when(volqueteService.findById(999L)).thenThrow(new VolqueteNotFoundException(999L));

        mockMvc.perform(get("/api/volquetes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void patch_cambiarEstado_returns200() throws Exception {
        String body = """
                {"estado":"EN_CLIENTE","origen":"MANUAL"}
                """;
        VolqueteResponse updated = new VolqueteResponse();
        updated.setId(1L);
        updated.setCodigoInterno("V-001");
        updated.setCodigoExterno("QR-V001");
        updated.setEstadoActual(VolqueteEstado.EN_CLIENTE);
        when(volqueteService.cambiarEstado(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(patch("/api/volquetes/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoActual").value("EN_CLIENTE"));
    }

    @Test
    void patch_cambiarEstado_sinOrigen_returns200() throws Exception {
        String body = """
                {"estado":"EN_TRANSITO"}
                """;
        VolqueteResponse updated = new VolqueteResponse();
        updated.setId(1L);
        updated.setEstadoActual(VolqueteEstado.EN_TRANSITO);
        when(volqueteService.cambiarEstado(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(patch("/api/volquetes/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoActual").value("EN_TRANSITO"));
    }

    @Test
    void patch_cambiarEstado_notFound_returns404() throws Exception {
        String body = """
                {"estado":"EN_CLIENTE"}
                """;
        when(volqueteService.cambiarEstado(eq(999L), any()))
                .thenThrow(new VolqueteNotFoundException(999L));

        mockMvc.perform(patch("/api/volquetes/999/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void get_export_returns200WithCsv() throws Exception {
        String csv = "Código Interno,Código Externo,Estado Actual\nV-001,QR-V001,DISPONIBLE\n";
        when(volqueteService.exportarInventario()).thenReturn(csv);

        mockMvc.perform(get("/api/volquetes/export"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Type", org.hamcrest.Matchers.containsString("text/csv")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(csv));
    }
}
