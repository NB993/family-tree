package io.jhchoe.familytree.common.auth.controller;

import io.jhchoe.familytree.common.auth.application.port.in.LogoutCommand;
import io.jhchoe.familytree.common.auth.application.port.in.LogoutUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.RefreshJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.RefreshJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.dto.LogoutResponse;
import io.jhchoe.familytree.common.auth.dto.TokenRefreshRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JWT 토큰 관련 API 컨트롤러입니다.
 * 토큰 갱신 및 로그아웃 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshJwtTokenUseCase refreshJwtTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    /**
     * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.
     * 
     * 기존 Refresh Token은 무효화되고 새로운 Refresh Token이 생성됩니다.
     * Access Token의 만료 시간은 5분으로 설정됩니다.
     *
     * @param request Refresh Token을 포함한 요청 데이터
     * @return 새로 발급된 JWT 토큰 정보 (Access Token + Refresh Token)
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtTokenResponse> refreshToken(
        @Valid @RequestBody TokenRefreshRequest request
    ) {
        log.debug("토큰 갱신 요청: [Refresh Token Length: {}]", 
            request.refreshToken() != null ? request.refreshToken().length() : 0);

        RefreshJwtTokenCommand command = new RefreshJwtTokenCommand(request.refreshToken());
        JwtTokenResponse response = refreshJwtTokenUseCase.refresh(command);

        log.debug("토큰 갱신 완료: [Access Token Length: {}, Refresh Token Length: {}]",
            response.accessToken().length(),
            response.refreshToken().length());

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 인증된 사용자를 로그아웃 처리합니다.
     * 
     * 해당 사용자의 모든 Refresh Token을 무효화하여 토큰 갱신을 차단합니다.
     * 기존 Access Token은 5분 후 자동으로 만료됩니다.
     *
     * @param user 현재 인증된 사용자
     * @return 로그아웃 처리 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
        @AuthenticationPrincipal FTUser user
    ) {
        log.debug("로그아웃 요청: [User ID: {}]", user.getId());

        LogoutCommand command = new LogoutCommand(user.getId());
        logoutUseCase.logout(command);

        log.debug("로그아웃 완료: [User ID: {}]", user.getId());

        return ResponseEntity.ok(LogoutResponse.createSuccess());
    }
}
