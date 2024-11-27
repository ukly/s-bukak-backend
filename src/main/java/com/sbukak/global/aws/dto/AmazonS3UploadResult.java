package com.sbukak.global.aws.dto;

public record AmazonS3UploadResult(
    String fileName,
    String fileUrl
) {
}
