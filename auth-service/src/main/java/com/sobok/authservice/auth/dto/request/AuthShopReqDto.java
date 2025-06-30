package com.sobok.authservice.auth.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthShopReqDto {

    @NotBlank(message = "아이디는 필수 입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 16자의 비밀번호여야 합니다.")
    private String password;

//    private String shopName; // 가게 이름
//    private String ownerName; // 가게 주인 이름
//    private String phone;
//    private String roadFull; // 도로명 주소
//    private Double latitude; // 위도
//    private Double longitude; // 경도
}
