package com.sobok.userservice.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPhoneDto {
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",  message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 16자의 비밀번호여야 합니다." )
    String phone;

    @Pattern(regexp = "^\\d{6}$", message = "6자리 숫자를 입력해줘.")
    String userInputCode;
}
