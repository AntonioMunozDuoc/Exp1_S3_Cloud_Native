package com.duoc.guiaservice.controller;

import com.duoc.guiaservice.model.GuiaDespacho;
import com.duoc.guiaservice.model.Asset;
import com.duoc.guiaservice.repository.GuiaDespachoRepository;
import com.duoc.guiaservice.service.AwsService;
import com.duoc.guiaservice.service.GuiaDespachoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/guias")
public class GuiaDespachoController {

    @Autowired
    private GuiaDespachoRepository repository;

    @Autowired
    private GuiaDespachoService guiaService;

    @Autowired
    private AwsService awsService;

    private final String BUCKET_NAME = "bucketsumativacloud";

    // 1. Crear guía de despacho
    @PostMapping("/crear")
    public ResponseEntity<GuiaDespacho> crearGuia(@RequestBody GuiaDespacho guia) {
        guia.setEstado("CREADA");
        return ResponseEntity.ok(repository.save(guia));
    }

    // 2. Subir guía generada a S3 (pasa por EFS primero)
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> subirGuia(@PathVariable Long id,
                                             @RequestParam("file") MultipartFile file) {
        try {
            String resultado = guiaService.procesarYSubirGuia(id, file);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // 3. Descargar guía desde S3 con validación de permisos (token básico)
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> descargarGuia(@PathVariable Long id,
                                                 @RequestHeader("Authorization") String token) {
        if (!token.equals("Bearer mi-token-secreto")) {
            return ResponseEntity.status(401).build();
        }

        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada con id: " + id));

        if (guia.getRutaS3() == null || guia.getRutaS3().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Asset asset = awsService.downloadFile(BUCKET_NAME, guia.getRutaS3());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + asset.getName().substring(asset.getName().lastIndexOf("/") + 1) + "\"")
                .contentType(MediaType.parseMediaType(
                        asset.getContentType() != null ? asset.getContentType() : "application/octet-stream"))
                .body(asset.getBytes());
    }

    // 4. Modificar/actualizar guía en S3
    @PutMapping("/{id}/update")
    public ResponseEntity<String> actualizarGuia(@PathVariable Long id,
                                                   @RequestParam("file") MultipartFile file) {
        try {
            GuiaDespacho guia = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Guía no encontrada con id: " + id));

            // Si ya existe en S3, borramos el anterior antes de subir el nuevo
            if (guia.getRutaS3() != null && !guia.getRutaS3().isEmpty()) {
                awsService.deleteFile(BUCKET_NAME, guia.getRutaS3());
            }

            String resultado = guiaService.procesarYSubirGuia(id, file);
            return ResponseEntity.ok("Guía actualizada correctamente. " + resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar: " + e.getMessage());
        }
    }

    // 5. Eliminar guía específica (de BD y de S3)
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> eliminarGuia(@PathVariable Long id) {
        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada con id: " + id));

        // Eliminar de S3 si tiene ruta
        if (guia.getRutaS3() != null && !guia.getRutaS3().isEmpty()) {
            awsService.deleteFile(BUCKET_NAME, guia.getRutaS3());
        }

        repository.deleteById(id);
        return ResponseEntity.ok("Guía con ID " + id + " eliminada de la base de datos y de S3.");
    }

    // 6. Consultar historial de guías por transportista y fecha
    @GetMapping("/buscar")
    public ResponseEntity<List<GuiaDespacho>> buscarGuias(
            @RequestParam String transportista,
            @RequestParam String fecha) {
        LocalDate fechaParseada = LocalDate.parse(fecha);
        List<GuiaDespacho> guias = repository.findByTransportistaAndFecha(transportista, fechaParseada);
        return ResponseEntity.ok(guias);
    }

    // 6b. Consultar historial completo (todas las guías)
    @GetMapping("/historial")
    public ResponseEntity<List<GuiaDespacho>> historialCompleto() {
        return ResponseEntity.ok(repository.findAll());
    }

    // 6c. Consultar historial por transportista (sin filtro de fecha)
    @GetMapping("/historial/{transportista}")
    public ResponseEntity<List<GuiaDespacho>> historialPorTransportista(
            @PathVariable String transportista) {
        return ResponseEntity.ok(repository.findByTransportista(transportista));
    }
}
