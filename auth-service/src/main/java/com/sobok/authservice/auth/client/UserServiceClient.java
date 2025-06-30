package com.sobok.authservice.auth.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import com.sobok.authservice.auth.dto.response.*;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/user/findByPhoneNumber")
    UserResDto findByPhone(@RequestBody String phoneNumber);
}
