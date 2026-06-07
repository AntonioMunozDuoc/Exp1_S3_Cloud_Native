package com.duoc.guiaservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "guias_despacho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transportista;

    @Column(nullable = false)
    private LocalDate fecha;

    // Guardaremos la ruta de S3
    private String rutaS3; 
    
    private String estado; // Para saber si esta en EFS o en S3
}