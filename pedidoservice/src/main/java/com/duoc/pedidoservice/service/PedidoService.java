package com.duoc.pedidoservice.service;

import com.duoc.pedidoservice.exception.RecursoNoEncontradoException;
import com.duoc.pedidoservice.model.Pedido;
import com.duoc.pedidoservice.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repo;

    public PedidoService(PedidoRepository repo) {
        this.repo = repo;
    }

    public List<Pedido> getAll() {
        return repo.findAll();
    }

    public Pedido getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Pedido no encontrado con id: " + id));
    }

    public Pedido save(Pedido p) {

        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del pedido es obligatorio");
        }

        if (p.getTransportistaNombre() == null || p.getTransportistaNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del transportista es obligatorio");
        }

        if (p.getCantidadItems() == null || p.getCantidadItems() <= 0) {
            throw new IllegalArgumentException("La cantidad de items debe ser mayor a 0");
        }

        if (p.getCostoTotal() == null || p.getCostoTotal() <= 0) {
            throw new IllegalArgumentException("El costo total debe ser mayor a 0");
        }

        return repo.save(p);
    }

    public Pedido update(Long id, Pedido p) {

        Pedido existente = getById(id);

        if (p.getNombre() != null && !p.getNombre().trim().isEmpty()) {
            existente.setNombre(p.getNombre());
        }

        if (p.getTransportistaNombre() != null && !p.getTransportistaNombre().trim().isEmpty()) {
            existente.setTransportistaNombre(p.getTransportistaNombre());
        }

        if (p.getCantidadItems() != null && p.getCantidadItems() > 0) {
            existente.setCantidadItems(p.getCantidadItems());
        }

        if (p.getCostoTotal() != null && p.getCostoTotal() > 0) {
            existente.setCostoTotal(p.getCostoTotal());
        }

        return repo.save(existente);
    }

    public void delete(Long id) {
        Pedido existente = getById(id);
        repo.delete(existente);
    }
}
