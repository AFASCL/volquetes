package com.afascl.volquetes.repository;

import com.afascl.volquetes.domain.Cliente;
import com.afascl.volquetes.domain.ClienteTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Listado liviano para selector (id, nombre, tipo). Sin N+1.
     */
    @Query("SELECT c.id as id, c.nombre as nombre, c.tipo as tipo FROM Cliente c ORDER BY c.nombre")
    List<ClienteSelectorProjection> findSelectorItems();

    interface ClienteSelectorProjection {
        Long getId();
        String getNombre();
        ClienteTipo getTipo();
    }
}
