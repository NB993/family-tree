package io.jhchoe.familytree.common.auth.controller;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 정보 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 현재 인증된 사용자의 정보를 반환합니다.
     *
     * @param user 현재 인증된 사용자
     * @return 사용자 프로필 정보
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal FTUser user) {
        log.debug("사용자 정보 요청: [User ID: {}]", user.getId());
        return ResponseEntity.ok(UserResponse.fromFTUser(user));
    }
}
