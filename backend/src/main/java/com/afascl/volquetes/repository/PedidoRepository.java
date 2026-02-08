package com.afascl.volquetes.repository;

import com.afascl.volquetes.domain.Pedido;
import com.afascl.volquetes.domain.PedidoEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    /**
     * Verifica si existe un pedido activo (NUEVO, ASIGNADO, ENTREGADO) con el volquete dado.
     */
    boolean existsByVolqueteIdAndEstadoIn(Long volqueteId, List<PedidoEstado> estados);

    /**
     * Verifica si existe otro pedido activo con el volquete dado (excluyendo el pedido con id dado).
     */
    boolean existsByVolqueteIdAndEstadoInAndIdNot(Long volqueteId, List<PedidoEstado> estados, Long excludePedidoId);
}
