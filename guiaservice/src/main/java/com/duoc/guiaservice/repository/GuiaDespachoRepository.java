package com.duoc.guiaservice.repository;

import com.duoc.guiaservice.model.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

    // Consultar guías por transportista y fecha específica
    List<GuiaDespacho> findByTransportistaAndFecha(String transportista, LocalDate fecha);

    // Consultar historial completo por transportista
    List<GuiaDespacho> findByTransportista(String transportista);
}
