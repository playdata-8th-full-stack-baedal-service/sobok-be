package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.dto.info.AuthBaseInfoResDto;

public interface AuthInfoProvider {
    AuthBaseInfoResDto getInfo(Long authId);
}
