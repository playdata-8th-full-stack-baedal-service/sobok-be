package com.sobok.apiservice.api.service.s3;


import com.sobok.apiservice.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.sobok.apiservice.common.util.Constants.internalServerErrorMsg;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final S3PutService s3PutService;
    private final S3CopyService s3CopyService;
    private final S3DeleteService s3DeleteService;
    private final S3UtilityService s3Util;


    /**
     * S3 이미지 업로드
     * @return 이미지의 url
     */
    public String uploadImage(MultipartFile image, String category) throws CustomException {
        log.info("S3 이미지 임시 업로드 서비스 시작 | category: {}",  category);

        return s3PutService.putImage(image, category, "profile".equals(category));
    }

    /**
     * <pre>
     *     S3 이미지 등록 확정
     *     1. URL에서 키 값 추출
     *     2. 기존 객체 정보 가져오기
     *     3. S3 COPY 요청
     * </pre>
     */
    public String registerImage(String url) {
        log.info("업로드 이미지 영구 변환 서비스 시작 | url: {}", url);

        // URL에서 키 값 가져오기
        String key = s3Util.detachImageUrl(url);

        try {
            return s3CopyService.copyImageToS3(key);
        } catch (Exception e) {
            log.error("이미지 복사 과정에서 오류가 발생했습니다.", e);
            throw new CustomException(internalServerErrorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * <pre>
     *  S3 기존 이미지 변경
     *  1. 기존 이미지 삭제
     *  2. 새로운 이미지 등록
     * </pre>
     * @return 새로운 이미지 등록
     */
    public String changeImage(MultipartFile image, String category, String oldPhoto) throws CustomException{
        log.info("S3 이미지 변경 서비스 시작 | category: {} | oldPhoto: {}",  category, oldPhoto);

        // oldPhoto가 유효하다면 삭제 진행
        if (oldPhoto != null && !oldPhoto.isEmpty()) { // TODO : 기본 이미지 설정 추가해야 함
            s3DeleteService.deleteS3Image(s3Util.detachImageUrl(oldPhoto));
        }

        // 새로운 이미지 등록 (TEMP 저장 X)
        return s3PutService.putImage(image, category, false);
    }

    public void deleteImage(String key) {
        s3DeleteService.deleteS3Image(key);
    }
}
