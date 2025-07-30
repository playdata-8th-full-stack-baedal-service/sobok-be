package com.sobok.userservice.user.controller;


import com.sobok.userservice.common.dto.ApiResponse;
import com.sobok.userservice.user.dto.email.UserEmailDto;
import com.sobok.userservice.user.dto.info.UserAddressDto;
import com.sobok.userservice.user.dto.request.*;
import com.sobok.userservice.user.dto.response.PreOrderUserResDto;
import com.sobok.userservice.user.dto.response.UserBookmarkResDto;
import com.sobok.userservice.user.dto.response.UserLikeResDto;
import com.sobok.userservice.user.service.UserService;
import com.sobok.userservice.common.dto.TokenUserInfo;
import com.sobok.userservice.user.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAddressService userAddressService;

    /**
     * 주소 저장
     */
    @PostMapping("/addAddress")
    public ResponseEntity<?> addAddress(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody @Valid UserAddressReqDto reqDto) {
        userAddressService.addAddress(userInfo.getId(), reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(), "성공적으로 주소가 저장되었습니다."));
    }

    /**
     * 주소 수정
     */
    @PatchMapping("/editAddress")
    public ResponseEntity<?> editAddress(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody UserAddressEditReqDto reqDto) {
        userAddressService.editAddress(userInfo.getId(), reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(), "성공적으로 주소가 변경되었습니다."));
    }

    /**
     * 주소 조회
     */
    @GetMapping("/getAddress")
    public ResponseEntity<?> getAddress(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<UserAddressDto> address = userAddressService.getAddress(userInfo.getId());
        return ResponseEntity.ok().body(ApiResponse.ok(address, "사용자의 주소를 성공적으로 조회하였습니다."));
    }

    /**
     * 주소 삭제
     */
    @DeleteMapping("/deleteAddress/{id}")
    public ResponseEntity<?> deleteAddress(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable Long id) {
        Long deletedId = userAddressService.deleteAddress(userInfo, id);
        return ResponseEntity.ok().body(ApiResponse.ok(deletedId, "사용자의 주소를 성공적으로 삭제하였습니다."));
    }

    /**
     * 이메일 수정
     */
    @PostMapping("/editEmail")
    public ResponseEntity<?> editEmail(@AuthenticationPrincipal TokenUserInfo userInfo, @Valid @RequestBody UserEmailDto reqDto) {
        userService.editEmail(userInfo, reqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(), "사용자의 이메일을 성공적으로 변경하였습니다."));
    }

    /**
     * 사용자 전화번호 변경
     */
    @PatchMapping("/editPhone")
    public ResponseEntity<?> editPhone(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody @Valid UserPhoneDto userPhoneDto) {
        userService.editPhone(userInfo, userPhoneDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userInfo.getId(), "사용자의 전화번호를 성공적으로 변경하였습니다."));
    }

    /**
     * 사용자의 요리 즐겨찾기 등록
     */
    @PostMapping("/addBookmark")
    public ResponseEntity<?> addBookmark(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody UserBookmarkReqDto userBookmarkReqDto) {
        userService.addBookmark(userInfo, userBookmarkReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userBookmarkReqDto, "해당 요리가 즐겨찾기에 등록되었습니다."));
    }

    /**
     * 사용자 요리 즐겨찾기 삭제
     */
    @PostMapping("/deleteBookmark")
    public ResponseEntity<?> deleteBookmark(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody UserBookmarkReqDto userBookmarkReqDto) {
        userService.deleteBookmark(userInfo, userBookmarkReqDto);
        return ResponseEntity.ok().body(ApiResponse.ok(userBookmarkReqDto, "해당 요리가 즐겨찾기 해제되었습니다."));
    }

    /**
     * 사용자 요리 즐겨찾기 조회
     */
    @GetMapping("/getBookmark")
    public ResponseEntity<?> getBookmark(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<UserBookmarkResDto> bookmark = userService.getBookmark(userInfo.getId());
        if (bookmark.isEmpty()) {
            return ResponseEntity.ok().body(ApiResponse.ok(null, HttpStatus.NO_CONTENT));  // 204
        }
        return ResponseEntity.ok().body(ApiResponse.ok(bookmark, "즐겨찾기 요리가 조회되었습니다."));
    }

    /**
     * 즐겨찾기 상태 조회
     */
    @GetMapping("/getBookmark/{id}")
    public ResponseEntity<?> getBookmarkById(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable(name = "id") Long cookId) {
        boolean response = userService.getBookmarkById(userInfo.getUserId(), cookId);
        return ResponseEntity.ok().body(ApiResponse.ok(response, "사용자의 즐겨찾기 상태가 조회되었습니다.."));
    }

    @GetMapping("/order-info")
    public ResponseEntity<?> preOrderUser(@AuthenticationPrincipal TokenUserInfo userInfo) {
        PreOrderUserResDto preOrderUser = userService.getPreOrderUser(userInfo);
        return ResponseEntity.ok().body(ApiResponse.ok(preOrderUser, "주문을 위한 사용자의 정보가 조회되었습니다."));
    }

    @PatchMapping("/editPhoto/{category}")
    public ResponseEntity<?> editPhoto(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestPart MultipartFile image, @PathVariable String category) {
        String url = userService.editPhoto(userInfo, image, category);
        return ResponseEntity.ok(ApiResponse.ok(url, "S3 버킷에 사진을 넣을 수 있는 URL이 성공적으로 발급되었습니다."));
    }

    /**
     * user 닉네임 중복 확인
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        userService.checkNickname(nickname);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 닉네임입니다."));
    }

    /**
     * email 중복 확인
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        userService.checkEmail(email);
        return ResponseEntity.ok(ApiResponse.ok(null, "사용 가능한 이메일입니다."));
    }

    /**
     * 게시글 좋아요 등록
     */
    @PostMapping("/user-like")
    public ResponseEntity<?> likePost(@RequestBody PostIdReqDto dto,
                                      @AuthenticationPrincipal TokenUserInfo userInfo) {
        UserLikeResDto res = userService.likePost(userInfo, dto.getPostId());
        return ResponseEntity.ok(ApiResponse.ok(res, "좋아요 등록 성공"));
    }

    /**
     * 게시글 좋아요 해제
     */
    @DeleteMapping("/user-unlike")
    public ResponseEntity<?> unlikePost(@RequestBody PostIdReqDto dto,
                                        @AuthenticationPrincipal TokenUserInfo userInfo) {
        UserLikeResDto res = userService.unlikePost(userInfo, dto.getPostId());
        return ResponseEntity.ok(ApiResponse.ok(res, "좋아요 해제 성공"));
    }

    /**
     * 사용자 좋아요 상태 조회
     */
    @GetMapping("/check-like")
    public ResponseEntity<?> checkPostLike(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestParam Long postId) {
        boolean result = userService.checkPostLike(userInfo, postId);
        return ResponseEntity.ok(ApiResponse.ok(result, "해당 사용자의 좋아요 상태가 정상적으로 조회되었습니다."));
    }

}
