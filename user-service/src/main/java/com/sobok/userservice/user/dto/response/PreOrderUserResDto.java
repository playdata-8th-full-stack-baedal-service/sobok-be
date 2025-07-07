package com.sobok.userservice.user.dto.response;

import com.sobok.userservice.user.dto.info.UserAddressDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreOrderUserResDto {
    private Long userId;
    private String nickname;
    private String phone;
    private List<UserAddressDto> addresses;
}
