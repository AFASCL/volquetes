package com.afascl.volquetes.service;

import com.afascl.volquetes.domain.Camion;
import com.afascl.volquetes.dto.CamionSelectorItemResponse;
import com.afascl.volquetes.repository.CamionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Selector de camiones para combos. T2 Backend. Contrato: GET /api/camiones/selector
 */
@Service
public class CamionService {

    private final CamionRepository camionRepository;

    public CamionService(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    @Transactional(readOnly = true)
    public List<CamionSelectorItemResponse> findSelectorItems() {
        return camionRepository.findAllByOrderByPatenteAsc().stream()
                .map(this::toSelectorItem)
                .collect(Collectors.toList());
    }

    private CamionSelectorItemResponse toSelectorItem(Camion c) {
        CamionSelectorItemResponse dto = new CamionSelectorItemResponse();
        dto.setId(c.getId());
        dto.setPatente(c.getPatente());
        return dto;
    }
}
