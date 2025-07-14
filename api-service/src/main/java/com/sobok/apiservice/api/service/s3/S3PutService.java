package com.sobok.apiservice.api.service.s3;

import com.sobok.apiservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static com.sobok.apiservice.common.util.Constants.*;
import static com.sobok.apiservice.common.util.S3Util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3PutService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    /**
     * <pre>
     *     새로운 S3 Image 업로드 (검증 과정 포함)
     *     1. 카테고리 입력값 검증
     *     2. 이미지가 진짜 이미지인지 검증
     *     3. PUT 요청
     * </pre>
     */
    public String putImage(MultipartFile image, String category, boolean isTempImg) {
        log.info("이미지 업로드 서비스 시작 | category: {}",  category);

        // 0. 카테고리 입력값 검증
        if (!ALLOWED_CATEGORIES.contains(category)) {
            log.error("유효하지 않은 카테고리 입력입니다. | category: {}", category);
            throw new CustomException("유효하지 않은 카테고리 입력입니다.", HttpStatus.BAD_REQUEST);
        }

        // 1. image가 없거나 이미지 이름이 없다면 오류
        if (image.isEmpty() || image.getOriginalFilename() == null) {
            log.error("빈 이미지가 요청되었습니다.");
            throw new CustomException(validationFailMsg, HttpStatus.BAD_REQUEST);
        }
        String name = image.getOriginalFilename();

        // 2. 이미지가 진짜 이미지인지 검증
        try {
            checkImageValidation(image, name);
        } catch (IOException e) {
            log.error("이미지를 바이트배열로 변환하는 과정에서 오류가 발생했습니다.");
            throw new CustomException(internalServerErrorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("알수 없는 오류가 발생하였습니다.", e);
            throw new CustomException(internalServerErrorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 3. PUT 요청
        try {
            return putImageToS3(image, category, isTempImg, name);
        } catch (Exception e) {
            log.error("이미지 업로드 과정에서 알 수 없는 오류 발생", e);
            throw new CustomException(internalServerErrorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * S3 PUT 요청
     */
    private String putImageToS3(MultipartFile image, String category, boolean isTempImg, String name) throws IOException {
        // PUT 요청 생성
        String fileName = getFileName(name, category, isTempImg);
        PutObjectRequest.Builder putRequestBuilder = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .metadata(getMetaData(name));

        // Temp 저장이라면 수명 설정
        if(isTempImg) putRequestBuilder.expires(Instant.now().plus(10L, ChronoUnit.MINUTES));

        // putRequest 생성
        PutObjectRequest putRequest = putRequestBuilder.build();

        // Request Body 생성
        RequestBody requestBody = RequestBody.fromInputStream(
                image.getInputStream(),
                image.getSize()
        );

        // S3 PUT 요청
        PutObjectResponse response = s3Client.putObject(putRequest, requestBody);
        log.info(response.toString());

        // 이미지 url 반환
        return getImageUrl(fileName);
    }
}
