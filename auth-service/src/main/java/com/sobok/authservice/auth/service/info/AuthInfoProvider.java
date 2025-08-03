package com.sobok.authservice.auth.service.info;

import com.sobok.authservice.auth.dto.info.AuthBaseInfoResDto;
import com.sobok.authservice.common.dto.TokenUserInfo;

public interface AuthInfoProvider {
    AuthBaseInfoResDto getInfo(TokenUserInfo tokenUserInfo);
}
