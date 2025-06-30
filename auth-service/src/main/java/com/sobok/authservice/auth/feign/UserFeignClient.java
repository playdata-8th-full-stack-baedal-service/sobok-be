package com.sobok.authservice.auth.feign;

import com.sobok.authservice.auth.dto.request.UserSignupReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// 일단 주석 처리 해두었습니다. 확인 후 삭제해도 됩니다.
//@FeignClient(name = "user-service")
//public interface UserFeignClient  {
//    @PostMapping("/api/signup")
//    ResponseEntity<Object> userSignup(@RequestBody UserSignupReqDto reqDto);
//}
