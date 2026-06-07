package com.duoc.guiaservice.repository;

import com.duoc.guiaservice.model.Asset;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class S3RepositoryImpl implements S3Repository {

    private final S3Client s3Client;

    // Inyeccion de dependencia del cliente S3 configurado previamente
    public S3RepositoryImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public List<String> listObjects(String bucketName) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    @Override
    public Asset getObject(String bucketName, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(request);
        
        return Asset.builder()
                .bytes(responseBytes.asByteArray())
                .name(key)
                .contentType(responseBytes.response().contentType())
                .build();
    }

    @Override
    public void putObject(String bucketName, String key, byte[] fileBytes, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(fileBytes));
    }

    @Override
    public void deleteObject(String bucketName, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}