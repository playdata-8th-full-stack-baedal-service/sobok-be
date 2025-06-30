package com.sobok.authservice.auth.feign;

import com.sobok.authservice.auth.dto.request.UserSignupReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "user-service")
public interface UserFeignClient  {
    @PostMapping("/api/signup")
    ResponseEntity<Object> userSignup(@RequestBody UserSignupReqDto reqDto);
}
