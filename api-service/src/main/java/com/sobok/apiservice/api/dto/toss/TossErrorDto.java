package com.sobok.apiservice.api.dto.toss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TossErrorDto {
    private String code;
    private String message;
}
