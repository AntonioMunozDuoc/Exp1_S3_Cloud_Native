package com.duoc.guiaservice.service;

import com.duoc.guiaservice.model.Asset;
import com.duoc.guiaservice.repository.S3Repository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AwsServiceImpl implements AwsService {

    private final S3Repository s3Repository;

    // Inyeccion del repositorio de S3
    public AwsServiceImpl(S3Repository s3Repository) {
        this.s3Repository = s3Repository;
    }

    @Override
    public List<String> listFiles(String bucketName) {
        return s3Repository.listObjects(bucketName);
    }

    @Override
    public Asset downloadFile(String bucketName, String key) {
        return s3Repository.getObject(bucketName, key);
    }

    @Override
    public void uploadFile(String bucketName, String key, byte[] fileBytes, String contentType) {
        s3Repository.putObject(bucketName, key, fileBytes, contentType);
    }

    @Override
    public void deleteFile(String bucketName, String key) {
        s3Repository.deleteObject(bucketName, key);
    }
}