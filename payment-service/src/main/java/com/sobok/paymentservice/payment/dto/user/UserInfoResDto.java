package com.sobok.paymentservice.payment.dto.user;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResDto {
    private String nickname;
    private String address;
    private String roadFull;
    private String phone;
}
