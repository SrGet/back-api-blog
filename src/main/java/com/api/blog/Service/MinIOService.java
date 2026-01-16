package com.api.blog.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinIOService {

    private final S3Client s3Client;
    private final String bucketName = "posts-images";

    public String uploadFile(MultipartFile file) {

        if(file == null || file.isEmpty()) {
            return null;
        }

        String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return key;
    }

    public void deleteFile(String keyFile){
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyFile)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (AwsServiceException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
