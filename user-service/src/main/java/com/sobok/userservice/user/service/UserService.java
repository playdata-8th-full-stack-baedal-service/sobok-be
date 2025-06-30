package com.sobok.userservice.user.service;

import com.sobok.userservice.common.exception.CustomException;
import com.sobok.userservice.user.dto.response.UserResDto;
import com.sobok.userservice.user.entity.User;
import com.sobok.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


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
                    .build();

        }else {
            throw new CustomException("해당 번호로 가입하신 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
