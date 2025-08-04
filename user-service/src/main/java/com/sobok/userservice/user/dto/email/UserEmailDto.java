package com.sobok.userservice.user.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEmailDto {
    @Schema(
            description = "사용자 이메일 주소",
            example = "example@email.com"
    )
    @Pattern(
            regexp = "^(?!.*\\.\\.)(?!.*\\.$)(?!^\\.)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "유효한 이메일 형식이어야 합니다."
    )
    private String email;

}
