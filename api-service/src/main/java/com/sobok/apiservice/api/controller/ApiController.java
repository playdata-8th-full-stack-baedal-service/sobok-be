package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.dto.address.LocationResDto;
import com.sobok.apiservice.api.dto.kakao.AuthLoginResDto;
import com.sobok.apiservice.api.dto.kakao.OauthResDto;
import com.sobok.apiservice.api.dto.kakao.KakaoUserResDto;
import com.sobok.apiservice.api.dto.toss.TossPayReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayResDto;
import com.sobok.apiservice.api.service.address.ConvertAddressService;
import com.sobok.apiservice.api.service.s3.S3Service;
import com.sobok.apiservice.api.service.s3.S3PutService;
import com.sobok.apiservice.api.service.socialLogin.KakaoLoginService;
import com.sobok.apiservice.api.service.toss.TossPayService;
import com.sobok.apiservice.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final S3Service s3Service;
    private final TossPayService tossPayService;
    private final ConvertAddressService convertAddressService;
    private final KakaoLoginService kakaoLoginService;


    /**
     * S3 사진 삭제
     */
    @DeleteMapping("/delete-S3-image")
    public ResponseEntity<?> deleteS3Image(@RequestParam String key) {
        s3Service.deleteImage(key);
        return ResponseEntity.ok().body(ApiResponse.ok(key, "S3의 파일이 성공적으로 삭제되었습니다."));
    }

    /**
     * S3 이미지 업로드 - 10분 내 register 필요
     */
    @PutMapping("/upload-image/{category}")
    public ResponseEntity<?> putS3Image(@RequestPart MultipartFile image, @PathVariable String category, @RequestParam Boolean notTemp) {
        String imgUrl = s3Service.uploadImage(image, category);
        return ResponseEntity.ok().body(ApiResponse.ok(imgUrl, "S3에 파일이 정상적으로 업로드되었습니다."));
    }

    /**
     * FEIGN
     * S3 이미지 등록 - 업로드 후 실제 정보 저장이 완료되면 실행
     */
    @PostMapping("/register-image")
    public String registerImg(@RequestParam String url) {
        return s3Service.registerImage(url);
    }

    /**
     * FEIGN
     * S3 이미지 변경
     */
    @PostMapping("/change-image")
    public String changeImage(@RequestPart MultipartFile image, @RequestPart String category, @RequestPart String oldPhoto) {
        return s3Service.changeImage(image, category, oldPhoto);
    }

    /**
     * 토스페이 결제
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody TossPayReqDto reqDto) {
        TossPayResDto resDto = tossPayService.confirmPayment(reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(resDto, "정상 처리되었습니다."));
    }

    @GetMapping("/convert-addr")
    public LocationResDto convertAddress(@RequestParam String roadFull) {
        return convertAddressService.getLocation(roadFull);
    }

    /**
     * 카카오 로그인/회원가입
     */
    // 카카오 콜백 요청 처리
    @GetMapping("/kakao-login")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        log.info("카카오 콜백 처리 시작! code: {}", code);
        // 인가코드로 액세스토큰 받기
        String kakaoAccessToken = kakaoLoginService.getKakaoAccessToken(code);
        // 액세스토큰으로 사용자 정보 받기
        KakaoUserResDto kakaoUserDto = kakaoLoginService.getKakaoUserInfo(kakaoAccessToken);

        // 회원가입 or 로그인 처리
        OauthResDto oauthResDto = kakaoLoginService.findOrCreateKakaoUser(kakaoUserDto);  //authId와 닉네임

        log.info("oauthResDto: {}", oauthResDto);

        String html;

        if (!oauthResDto.isNew()) {
            log.info("jwt 토큰 생성 시작");

            // JWT 토큰 생성 (우리 사이트 로그인 유지를 위해. 사용자 정보를 위해.)
            AuthLoginResDto authLoginResDto = kakaoLoginService.kakaoLoginToken(oauthResDto.getAuthId());

            log.info("authLoginResDto: {}", authLoginResDto);

            // 팝업 닫기 HTML 응답
            html = String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head><title>카카오 로그인 완료</title></head>
                    <body>
                        <script>
                            if (window.opener) {
                                window.opener.postMessage({
                                    type: 'OAUTH_SUCCESS',
                                    token: '%s',
                                    id: '%s',
                                    role: '%s',
                                    provider: 'KAKAO'
                                }, window.location.origin);
                                window.close();
                            } else {
                                window.location.href = 'http://localhost:5173';
                            }
                        </script>
                        <p>카카오 로그인 처리 중...</p>
                    </body>
                    </html>
                    """, authLoginResDto.getAccessToken(), oauthResDto.getId(), authLoginResDto.getRole());
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(html);
        } else {
            log.info("새로운 사용자입니다. 추가 회원가입을 진행합니다.");
            // 프론트엔드에 '신규 가입자'임을 알리고 추가 정보 입력 페이지로 이동하도록 메시지를 보냅니다.
            // 이때 카카오에서 받은 정보(닉네임 등)를 함께 넘겨주어 회원가입 폼을 미리 채울 수 있습니다.
            html = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head><title>회원가입</title></head>
                            <body>
                                <script>
                                    if (window.opener) {
                                        window.opener.postMessage({
                                            type: 'NEW_USER_SIGNUP',
                                            oauthId: '%s',      // oauth ID
                                            nickname: '%s',     // 카카오 닉네임 (가입 폼에 미리 채울 수 있음)
                                            email: '%s',
                                            provider: 'KAKAO'
                                        }, 'http://localhost:5173');
                                        window.close();
                                    } else {
                                        window.location.href = 'http://localhost:5173/auth/signup/kakao-usersignup?provider=KAKAO&kakaoId=%s';
//                                        window.location.href = `http://localhost:5173/auth/signup/kakao-usersignup?provider=KAKAO&oauthId=${oauthResDto.getId()}&nickname=${encodeURIComponent(kakaoUserDto.getProperties().getNickname())}&email=${encodeURIComponent(kakaoUserDto.getAccount().getEmail())}`;
                                    }
                                </script>
                                <p>회원가입 페이지로 이동 중...</p>
                            </body>
                            </html>
                            """, oauthResDto.getId(), kakaoUserDto.getProperties().getNickname(),
                    kakaoUserDto.getAccount().getEmail(), kakaoUserDto.getId());
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(html);
        }
    }

    //feign요청으로 들어올 api
    @GetMapping("/findByOauthId")
    public OauthResDto findByOauthId(@RequestParam("id") Long oauthId) {
        return kakaoLoginService.findOauth(oauthId);
    }

}