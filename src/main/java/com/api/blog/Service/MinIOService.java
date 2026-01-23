package com.api.blog.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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
            log.info("File uploading successful");
        } catch (IOException e) {
            log.error("File uploading failed. Reason: {}",e.getMessage());
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
            log.info("File deleting successful");
        } catch (AwsServiceException e) {
            log.error("File deleting failed. Reason: {}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    public InputStream getImg(String key){

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        log.info("Returning object for key: {}",key);
        return s3Client.getObject(getObjectRequest);
    }

}
