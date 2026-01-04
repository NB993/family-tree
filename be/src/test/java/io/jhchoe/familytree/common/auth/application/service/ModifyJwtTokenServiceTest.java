package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.ModifyJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.config.JwtProperties;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.auth.exception.InvalidTokenException;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.test.fixture.UserFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JWT 토큰 수정 서비스 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] ModifyJwtTokenServiceTest")
class ModifyJwtTokenServiceTest {

    @InjectMocks
    private ModifyJwtTokenService modifyJwtTokenService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private SaveRefreshTokenUseCase saveRefreshTokenUseCase;

    @Mock
    private DeleteJwtTokenUseCase deleteJwtTokenUseCase;

    @Mock
    private FindUserPort findUserPort;

    @Test
    @DisplayName("유효한 Refresh Token으로 토큰 갱신 시 새로운 토큰이 반환됩니다")
    void modify_returns_new_tokens_when_valid_refresh_token() {
        // given
        String validRefreshToken = "valid.refresh.token";
        ModifyJwtTokenCommand command = new ModifyJwtTokenCommand(validRefreshToken);
        
        Long userId = 1L;
        String email = "test@example.com";
        String name = "테스트사용자";
        String profileUrl = "https://example.com";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";
        Long accessTokenExpiration = 3600L;
        Long refreshTokenExpiration = 604800L;

        User user = UserFixture.withId(1L);

        // Mocking: Refresh Token이 유효한 토큰임을 모킹
        when(jwtTokenUtil.validateToken(validRefreshToken)).thenReturn(true);
        
        // Mocking: 토큰에서 사용자 정보 추출 모킹
        when(jwtTokenUtil.extractUserId(validRefreshToken)).thenReturn(userId);
        when(jwtTokenUtil.extractEmail(validRefreshToken)).thenReturn(email);
        when(jwtTokenUtil.extractName(validRefreshToken)).thenReturn(name);
        
        // Mocking: 새로운 토큰 생성 모킹
        when(jwtTokenUtil.generateAccessToken(any(FTUser.class))).thenReturn(newAccessToken);
        when(jwtTokenUtil.generateRefreshToken(anyLong())).thenReturn(newRefreshToken);
        
        // Mocking: 토큰 만료 시간 조회 모킹
        when(jwtProperties.getAccessTokenExpiration()).thenReturn(accessTokenExpiration);
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(refreshTokenExpiration);
        
        // Mocking: RefreshToken 삭제 및 저장 모킹
        doNothing().when(deleteJwtTokenUseCase).delete(any());
        when(saveRefreshTokenUseCase.save(any(SaveRefreshTokenCommand.class))).thenReturn(anyLong());

        // Mocking: User 조회 모킹
        when(findUserPort.findById(1L)).thenReturn(Optional.of(user));

        // when
        JwtTokenResponse response = modifyJwtTokenService.modify(command);

        // then
        assertThat(response.accessToken()).isEqualTo(newAccessToken);
        assertThat(response.refreshToken()).isEqualTo(newRefreshToken);
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(accessTokenExpiration);
        
        verify(deleteJwtTokenUseCase).delete(any());
        verify(saveRefreshTokenUseCase).save(any());
    }

    @Test
    @DisplayName("유효하지 않은 Refresh Token으로 갱신 시 InvalidTokenException이 발생합니다")
    void modify_throws_exception_when_invalid_refresh_token() {
        // given
        String invalidRefreshToken = "invalid.refresh.token";
        ModifyJwtTokenCommand command = new ModifyJwtTokenCommand(invalidRefreshToken);

        // Mocking: Refresh Token이 유효하지 않으면 예외를 던지도록 모킹
        when(jwtTokenUtil.validateToken(invalidRefreshToken)).thenThrow(new FTException(AuthExceptionCode.INVALID_TOKEN_FORMAT));

        // when & then
        assertThatThrownBy(() -> modifyJwtTokenService.modify(command))
            .isInstanceOf(FTException.class)
            .extracting("code")
            .isEqualTo("A005");
    }

    @Test
    @DisplayName("null command로 토큰 갱신 시 NullPointerException이 발생합니다")
    void modify_throws_exception_when_command_is_null() {
        // when & then
        assertThatThrownBy(() -> modifyJwtTokenService.modify(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }
}
