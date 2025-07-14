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

import static com.sobok.apiservice.common.util.S3Util.getImageUrl;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3CopyService {
    private final S3Client s3Client;

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
                .metadata(headResponse.metadata())
                .contentType(headResponse.contentType())
                .build();

        // S3 COPY 요청
        s3Client.copyObject(copyObjectRequest);
        return getImageUrl(key);
    }
}
