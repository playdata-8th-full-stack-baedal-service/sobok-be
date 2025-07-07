package com.sobok.postservice.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenUserInfo {
    Long id;
    String role;
    Long userId;
    Long riderId;
    Long shopId;
}
