package com.duoc.pedidoservice.repository;

import com.duoc.pedidoservice.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

/// Repositorio encargado de las operaciones de persistencia
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
