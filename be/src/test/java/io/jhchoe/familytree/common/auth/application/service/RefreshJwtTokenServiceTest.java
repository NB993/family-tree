package io.jhchoe.familytree.common.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.RefreshJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.config.JwtProperties;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.exception.InvalidTokenException;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] RefreshJwtTokenServiceTest")
class RefreshJwtTokenServiceTest {

    @InjectMocks
    private RefreshJwtTokenService refreshJwtTokenService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private SaveRefreshTokenUseCase saveRefreshTokenUseCase;

    @Mock
    private DeleteRefreshTokenUseCase deleteRefreshTokenUseCase;

    @Test
    @DisplayName("유효한 Refresh Token으로 토큰 갱신에 성공합니다")
    void refresh_success_when_valid_refresh_token() {
        // given
        String validRefreshToken = "valid.refresh.token";
        Long userId = 1L;
        String email = "test@example.com";
        String name = "Test User";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";
        Long accessTokenExpiration = 3600L;
        Long refreshTokenExpiration = 604800L;

        RefreshJwtTokenCommand command = new RefreshJwtTokenCommand(validRefreshToken);

        // Mocking: 토큰 검증 성공
        when(jwtTokenUtil.validateToken(validRefreshToken)).thenReturn(true);
        // Mocking: 토큰에서 사용자 정보 추출
        when(jwtTokenUtil.extractUserId(validRefreshToken)).thenReturn(userId);
        when(jwtTokenUtil.extractEmail(validRefreshToken)).thenReturn(email);
        when(jwtTokenUtil.extractName(validRefreshToken)).thenReturn(name);
        // Mocking: 새로운 토큰 생성
        when(jwtTokenUtil.generateAccessToken(any())).thenReturn(newAccessToken);
        when(jwtTokenUtil.generateRefreshToken(userId)).thenReturn(newRefreshToken);
        // Mocking: 토큰 만료 시간
        when(jwtProperties.getAccessTokenExpiration()).thenReturn(accessTokenExpiration);
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(refreshTokenExpiration);

        // when
        JwtTokenResponse response = refreshJwtTokenService.refresh(command);

        // then
        assertThat(response.accessToken()).isEqualTo(newAccessToken);
        assertThat(response.refreshToken()).isEqualTo(newRefreshToken);
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(accessTokenExpiration);

        // 기존 토큰 무효화 확인
        verify(deleteRefreshTokenUseCase).delete(any(DeleteRefreshTokenCommand.class));
        // 새로운 토큰 저장 확인
        verify(saveRefreshTokenUseCase).save(any(SaveRefreshTokenCommand.class));
    }

    @Test
    @DisplayName("유효하지 않은 Refresh Token인 경우 InvalidTokenException이 발생합니다")
    void throw_exception_when_invalid_refresh_token() {
        // given
        String invalidRefreshToken = "invalid.refresh.token";
        RefreshJwtTokenCommand command = new RefreshJwtTokenCommand(invalidRefreshToken);

        // Mocking: 토큰 검증 실패
        when(jwtTokenUtil.validateToken(invalidRefreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> refreshJwtTokenService.refresh(command))
            .isInstanceOf(InvalidTokenException.class)
            .hasMessage("유효하지 않은 Refresh Token입니다");
    }

    @Test
    @DisplayName("command가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_command_is_null() {
        assertThatThrownBy(() -> refreshJwtTokenService.refresh(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }
}
