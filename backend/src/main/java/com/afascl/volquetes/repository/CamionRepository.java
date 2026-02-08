package com.afascl.volquetes.repository;

import com.afascl.volquetes.domain.Camion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CamionRepository extends JpaRepository<Camion, Long> {

    List<Camion> findAllByOrderByPatenteAsc();
}
