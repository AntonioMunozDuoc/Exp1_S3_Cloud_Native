package com.duoc.guiaservice.repository;

import com.duoc.guiaservice.model.Asset;
import java.util.List;

public interface S3Repository {
    
    // listar todos los nombres de archivos dentro de un bucket
    List<String> listObjects(String bucketName);
    
    // obtener un archivo empaquetado como Asset desde S3
    Asset getObject(String bucketName, String key);
    
    // subir un archivo al bucket en una ruta específica
    void putObject(String bucketName, String key, byte[] fileBytes, String contentType);
    
    // eliminar un objeto del bucket
    void deleteObject(String bucketName, String key);
}