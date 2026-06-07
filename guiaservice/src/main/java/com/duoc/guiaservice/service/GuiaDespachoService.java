package com.duoc.guiaservice.service;

import com.duoc.guiaservice.model.GuiaDespacho;
import com.duoc.guiaservice.repository.GuiaDespachoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class GuiaDespachoService {

    @Autowired
    private GuiaDespachoRepository repository;

    @Autowired
    private AwsService awsService;

    private final String BUCKET_NAME = "bucketsumativacloud";

    // Ruta temporal de Amazon EFS montada en la instancia EC2
    private final String EFS_PATH = "/mnt/efs/";

    public String procesarYSubirGuia(Long id, MultipartFile file) throws IOException {
        GuiaDespacho guia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada con id: " + id));

        // 1. Guardar temporalmente en EFS
        Path directorioEfs = Paths.get(EFS_PATH);
        if (!Files.exists(directorioEfs)) {
            Files.createDirectories(directorioEfs); // Crea la carpeta EFS si no existe (entorno local)
        }

        Path rutaTemporal = directorioEfs.resolve(file.getOriginalFilename());
        Files.write(rutaTemporal, file.getBytes());

        guia.setEstado("GUARDADO_EN_EFS");
        repository.save(guia);

        // 2. Armar la ruta dinámica para S3: /yyyyMM/transportista/nombreArchivo
        String anioMes = guia.getFecha().getYear()
                + String.format("%02d", guia.getFecha().getMonthValue());
        String rutaS3 = anioMes + "/" + guia.getTransportista() + "/" + file.getOriginalFilename();

        // 3. Leer el archivo desde EFS y subir a S3
        byte[] bytesDesdeEfs = Files.readAllBytes(rutaTemporal);
        awsService.uploadFile(
                BUCKET_NAME,
                rutaS3,
                bytesDesdeEfs,
                file.getContentType()
        );

        // 4. Actualizar metadata en la base de datos Oracle
        guia.setRutaS3(rutaS3);
        guia.setEstado("SUBIDO_A_S3");
        repository.save(guia);

        // Opcional: eliminar del EFS tras subir exitosamente a S3
        // Files.deleteIfExists(rutaTemporal);

        return "Archivo guardado en EFS (" + rutaTemporal + ") y subido a S3 en la ruta: " + rutaS3;
    }
}
