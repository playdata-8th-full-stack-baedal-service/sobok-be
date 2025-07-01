package com.sobok.authservice.auth.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthShopReqDto {

    @NotBlank(message = "아이디는 필수 입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 16자의 비밀번호여야 합니다.")
    private String password;

    @NotBlank(message = "가게 이름은 필수 입니다.")
    private String shopName; // 가게 이름

    @NotBlank(message = "가게 주인 이름 작성은 필수 입니다.")
    private String ownerName; // 가게 주인 이름

    @NotBlank(message = "전화번호는 필수 입니다.")
    @Pattern(regexp = "^01[016789]\\d{8}$", message = "전화번호는 - 없이 숫자만 11자리여야 합니다.")
    private String phone;

    @NotBlank(message = "도로명 주소는 필수 입니다.")
    private String roadFull; // 도로명 주소

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private Double latitude; // 위도

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private Double longitude; // 경도
}
