package com.afascl.volquetes.repository;

import com.afascl.volquetes.domain.Chofer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChoferRepository extends JpaRepository<Chofer, Long> {

    List<Chofer> findAllByOrderByNombreAsc();
}
