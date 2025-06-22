package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.out.DeleteRefreshTokenPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * JWT 토큰 삭제 서비스 단위 테스트
 * 로그아웃 기능을 담당하는 DeleteJwtTokenService를 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] DeleteJwtTokenServiceTest")
class DeleteJwtTokenServiceTest {

    @InjectMocks
    private DeleteJwtTokenService deleteJwtTokenService;

    @Mock
    private DeleteRefreshTokenPort deleteRefreshTokenPort;

    @Test
    @DisplayName("유효한 사용자 ID로 JWT 토큰 삭제 시 성공합니다")
    void delete_success_when_valid_user_id() {
        // given
        Long userId = 1L;
        DeleteRefreshTokenCommand command = new DeleteRefreshTokenCommand(userId);

        // Mocking: RefreshToken 삭제 성공
        doNothing().when(deleteRefreshTokenPort).deleteByUserId(userId);

        // when
        deleteJwtTokenService.delete(command);

        // then
        verify(deleteRefreshTokenPort).deleteByUserId(userId);
    }

    @Test
    @DisplayName("null command로 JWT 토큰 삭제 시 NullPointerException이 발생합니다")
    void delete_throws_exception_when_command_is_null() {
        // when & then
        assertThatThrownBy(() -> deleteJwtTokenService.delete(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }
}
