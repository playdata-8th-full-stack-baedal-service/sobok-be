package com.sobok.apiservice.api.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.MetadataDirective;


@Service
@Slf4j
@RequiredArgsConstructor
public class S3CopyService {
    private final S3Client s3Client;
    private final S3UtilityService s3Util;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3 COPY 요청
     */
    public String copyImageToS3(String key) {
        String tempKey = "temp/" + key;

        // 기존 객체 정보 가져오기
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(tempKey)
                .build();

        HeadObjectResponse headResponse = s3Client.headObject(headRequest);

        // COPY 요청 생성
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(tempKey)
                .destinationBucket(bucket)
                .destinationKey(key)
                .metadataDirective(MetadataDirective.REPLACE)
                .contentType(s3Util.getContentType(key))
                .contentDisposition("inline")
                .metadata(headResponse.metadata())
                .contentType(headResponse.contentType())
                .build();

        // S3 COPY 요청
        s3Client.copyObject(copyObjectRequest);
        return s3Util.returnImageUrl(key);
    }
}
