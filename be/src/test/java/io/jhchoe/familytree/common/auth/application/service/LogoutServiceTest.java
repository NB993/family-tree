package io.jhchoe.familytree.common.auth.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.LogoutCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] LogoutServiceTest")
class LogoutServiceTest {

    @InjectMocks
    private LogoutService logoutService;

    @Mock
    private DeleteRefreshTokenUseCase deleteRefreshTokenUseCase;

    @Test
    @DisplayName("유효한 userId로 로그아웃에 성공합니다")
    void logout_success_when_valid_user_id() {
        // given
        Long userId = 1L;
        LogoutCommand command = new LogoutCommand(userId);

        // when
        logoutService.logout(command);

        // then
        // Refresh Token 삭제 확인
        verify(deleteRefreshTokenUseCase).delete(any(DeleteRefreshTokenCommand.class));
    }

    @Test
    @DisplayName("command가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_command_is_null() {
        assertThatThrownBy(() -> logoutService.logout(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }
}
