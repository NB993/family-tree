package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenUseCase;
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
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] DeleteJwtTokenServiceTest")
class DeleteJwtTokenServiceTest {

    @InjectMocks
    private DeleteJwtTokenService deleteJwtTokenService;

    @Mock
    private DeleteRefreshTokenUseCase deleteRefreshTokenUseCase;

    @Test
    @DisplayName("유효한 사용자 ID로 JWT 토큰 삭제 시 성공합니다")
    void delete_success_when_valid_user_id() {
        // given
        Long userId = 1L;
        DeleteJwtTokenCommand command = new DeleteJwtTokenCommand(userId);

        // Mocking: RefreshToken 삭제 모킹
        doNothing().when(deleteRefreshTokenUseCase).delete(any());

        // when
        deleteJwtTokenService.delete(command);

        // then
        verify(deleteRefreshTokenUseCase).delete(any());
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
