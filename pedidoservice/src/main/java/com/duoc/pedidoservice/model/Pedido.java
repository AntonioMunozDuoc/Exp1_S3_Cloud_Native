package com.duoc.pedidoservice.model;

import jakarta.persistence.*;
import lombok.*;

// Entidad que representa un pedido en el sistema.
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String transportistaNombre;
    private Integer cantidadItems;
    private Integer costoTotal;
}
