package com.sbukak.global.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sbukak.global.aws.dto.AmazonS3UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public AmazonS3UploadResult upload(MultipartFile file, String category) {
        String key = category + "/" + UUID.randomUUID().toString().substring(0, 10) + "_" + file.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucketName, key, file.getInputStream(), objectMetadata);
            String fileUrl = amazonS3.getUrl(bucketName, key).toString();
            return new AmazonS3UploadResult(file.getOriginalFilename(), fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
