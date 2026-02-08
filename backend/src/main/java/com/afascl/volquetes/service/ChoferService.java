package com.afascl.volquetes.service;

import com.afascl.volquetes.domain.Chofer;
import com.afascl.volquetes.dto.ChoferSelectorItemResponse;
import com.afascl.volquetes.repository.ChoferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Selector de choferes para combos. T2 Backend. Contrato: GET /api/choferes/selector
 */
@Service
public class ChoferService {

    private final ChoferRepository choferRepository;

    public ChoferService(ChoferRepository choferRepository) {
        this.choferRepository = choferRepository;
    }

    @Transactional(readOnly = true)
    public List<ChoferSelectorItemResponse> findSelectorItems() {
        return choferRepository.findAllByOrderByNombreAsc().stream()
                .map(this::toSelectorItem)
                .collect(Collectors.toList());
    }

    private ChoferSelectorItemResponse toSelectorItem(Chofer c) {
        ChoferSelectorItemResponse dto = new ChoferSelectorItemResponse();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        return dto;
    }
}
