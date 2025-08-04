package com.sobok.authservice.auth.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthFindIdReqDto {
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호 형식이 유효하지 않습니다.")
    String userPhoneNumber;
    String userInputCode;
}
