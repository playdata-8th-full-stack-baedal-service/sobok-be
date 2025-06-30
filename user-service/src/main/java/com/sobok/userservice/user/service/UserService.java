package com.sobok.userservice.user.service;

import com.sobok.userservice.user.dto.request.UserSignupReqDto;
import com.sobok.userservice.user.entity.Users;
import com.sobok.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public void signup(UserSignupReqDto reqDto) {
        // null safe 한 값 먼저 담기
        Users.UsersBuilder userBuilder = Users.builder()
                .authId(reqDto.getAuthId())
                .nickname(reqDto.getNickname())
                .phone(reqDto.getPhone())
                .photo(reqDto.getPhoto());

        // 이메일이 null이 아니라면 email도 담기
        if(reqDto.getEmail() != null) {
            userBuilder.email(reqDto.getEmail());
        }

        // user 객체 생성
        Users user = userBuilder.build();

        // user DB에 저장
        userRepository.save(user);
    }
}
