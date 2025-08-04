package com.sobok.userservice.user.dto.response;

import com.sobok.userservice.user.dto.info.UserAddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 전 사용자 정보 응답 DTO")
public class PreOrderUserResDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "사용자 닉네임", example = "바나나우유")
    private String nickname;
    @Schema(description = "전화번호", example = "01012345678")
    private String phone;
    @Schema(description = "이메일 주소", example = "example@email.com")
    private String email;
    @Schema(description = "주소 목록")
    private List<UserAddressDto> addresses;
}
