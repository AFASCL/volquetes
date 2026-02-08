package com.afascl.volquetes.repository;

import com.afascl.volquetes.domain.Volquete;
import com.afascl.volquetes.domain.VolqueteEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolqueteRepository extends JpaRepository<Volquete, Long> {

    Page<Volquete> findByEstadoActual(VolqueteEstado estadoActual, Pageable pageable);

    boolean existsByCodigoInterno(String codigoInterno);

    boolean existsByCodigoExterno(String codigoExterno);

    boolean existsByCodigoInternoAndIdNot(String codigoInterno, Long id);

    boolean existsByCodigoExternoAndIdNot(String codigoExterno, Long id);
}
