package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.out.SaveRefreshTokenPort;
import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] SaveRefreshTokenServiceTest")
class SaveRefreshTokenServiceTest {

    @InjectMocks
    private SaveRefreshTokenService saveRefreshTokenService;
    
    @Mock
    private SaveRefreshTokenPort saveRefreshTokenPort;

    @Test
    @DisplayName("유효한 커맨드로 토큰 저장 시 ID를 반환합니다")
    void return_id_when_command_is_valid() {
        // given
        Long userId = 1L;
        String tokenHash = "hashed-token-value";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        SaveRefreshTokenCommand command = new SaveRefreshTokenCommand(userId, tokenHash, expiresAt);
        Long expectedId = 123L;
        
        // Mocking: RefreshToken 저장 모킹
        when(saveRefreshTokenPort.save(any(RefreshToken.class))).thenReturn(expectedId);
        
        // when
        Long actualId = saveRefreshTokenService.save(command);
        
        // then
        assertThat(actualId).isEqualTo(expectedId);
        verify(saveRefreshTokenPort).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("커맨드가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_command_is_null() {
        assertThatThrownBy(() -> saveRefreshTokenService.save(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("도메인 객체를 올바르게 생성하여 저장합니다")
    void newRefreshToken_domain_object_correctly() {
        // given
        Long userId = 1L;
        String tokenHash = "hashed-token-value";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        SaveRefreshTokenCommand command = new SaveRefreshTokenCommand(userId, tokenHash, expiresAt);
        Long expectedId = 123L;
        
        // Mocking: RefreshToken 저장 모킹
        when(saveRefreshTokenPort.save(any(RefreshToken.class))).thenReturn(expectedId);
        
        // when
        saveRefreshTokenService.save(command);
        
        // then
        verify(saveRefreshTokenPort).save(argThat(token -> 
            token.getUserId().equals(userId) &&
            token.getTokenHash().equals(tokenHash) &&
            token.getExpiresAt().equals(expiresAt)
        ));
    }
}
