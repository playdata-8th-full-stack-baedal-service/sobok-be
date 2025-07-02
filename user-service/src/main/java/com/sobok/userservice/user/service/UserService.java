package com.sobok.userservice.user.service;

import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.dto.request.UserSignupReqDto;
import com.sobok.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.dto.response.UserResDto;
import com.sobok.userservice.user.entity.User;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserAddressService userAddressService;

  public UserResDto findByPhoneNumber(String phoneNumber) {
        Optional<User> byPhone = userRepository.findByPhone(phoneNumber);
        if (byPhone.isPresent()) {
            User user = byPhone.get();
            log.info("전화번호로 얻어온 auth의 정보: {}", byPhone.toString());
            return UserResDto.builder()
                    .id(user.getId())
                    .authId(user.getAuthId())
                    .nickname(user.getNickname())
                    .photo(user.getPhoto())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .build();

        }else {
            log.info("해당 번호로 가입하신 정보가 없습니다.");
            return null;
        }
  }

    /**
     * <pre>
     *     # 사용자 회원가입
     *     1. 사용자 객체 생성 후 저장
     *     2. 주소 값이 전달되었다면 사용자 주소도 저장
     * </pre>
     *
     */
    public void signup(UserSignupReqDto reqDto) {
        log.info("사용자 회원가입 시작 : {}", reqDto.getAuthId());

        // 유저 객체 생성
        User user = User.builder()
                .authId(reqDto.getAuthId())
                .nickname(reqDto.getNickname())
                .phone(reqDto.getPhone())
                .photo(reqDto.getPhoto())
                .email(reqDto.getEmail())
                .build();


        // user DB에 저장
        userRepository.save(user);

        // 사용자 주소 저장
        if (reqDto.getRoadFull() != null) {
            UserAddressReqDto addrDto = UserAddressReqDto.builder()
                    .roadFull(reqDto.getRoadFull())
                    .addrDetail(reqDto.getAddrDetail())
                    .build();

            userAddressService.addAddress(reqDto.getAuthId(), addrDto);
            log.info("성공적으로 사용자의 주소를 저장했습니다.");
        }

        log.info("성공적으로 사용자 회원가입이 완료되었습니다.");

    }
}
