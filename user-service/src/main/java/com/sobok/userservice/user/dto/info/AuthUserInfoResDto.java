package com.sobok.userservice.user.dto.info;

import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUserInfoResDto {
    String loginId;
    String nickname;
    String email;
    String phone;
    String photo;
    List<UserAddressDto> addresses;
    Long authId;
}
