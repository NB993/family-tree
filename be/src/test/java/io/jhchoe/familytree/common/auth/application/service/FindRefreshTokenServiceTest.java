package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.FindExpiredRefreshTokensQuery;
import io.jhchoe.familytree.common.auth.application.port.in.FindRefreshTokenByUserIdQuery;
import io.jhchoe.familytree.common.auth.application.port.out.FindRefreshTokenPort;
import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FindRefreshTokenServiceTest")
class FindRefreshTokenServiceTest {

    @InjectMocks
    private FindRefreshTokenService findRefreshTokenService;
    
    @Mock
    private FindRefreshTokenPort findRefreshTokenPort;

    @Test
    @DisplayName("유효한 사용자 ID로 조회 시 RefreshToken을 반환합니다")
    void return_refresh_token_when_user_id_is_valid() {
        // given
        Long userId = 1L;
        FindRefreshTokenByUserIdQuery query = new FindRefreshTokenByUserIdQuery(userId);
        RefreshToken expectedToken = RefreshToken.create(
            userId, "hashed-token", LocalDateTime.now().plusDays(7)
        );
        
        // Mocking: 유효한 사용자 ID로 RefreshToken 조회 모킹
        when(findRefreshTokenPort.findByUserId(userId)).thenReturn(Optional.of(expectedToken));
        
        // when
        Optional<RefreshToken> actualToken = findRefreshTokenService.findByUserId(query);
        
        // then
        assertThat(actualToken).isPresent();
        assertThat(actualToken.get()).isEqualTo(expectedToken);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 시 빈 Optional을 반환합니다")
    void return_empty_when_user_not_found() {
        // given
        Long userId = 999L;
        FindRefreshTokenByUserIdQuery query = new FindRefreshTokenByUserIdQuery(userId);
        
        // Mocking: 존재하지 않는 사용자 ID 조회 모킹
        when(findRefreshTokenPort.findByUserId(userId)).thenReturn(Optional.empty());
        
        // when
        Optional<RefreshToken> actualToken = findRefreshTokenService.findByUserId(query);
        
        // then
        assertThat(actualToken).isEmpty();
    }

    @Test
    @DisplayName("만료된 토큰들을 조회 시 토큰 목록을 반환합니다")
    void return_expired_tokens_when_query_is_valid() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now();
        FindExpiredRefreshTokensQuery query = new FindExpiredRefreshTokensQuery(currentDateTime);
        List<RefreshToken> expectedTokens = List.of(
            RefreshToken.create(1L, "token1", currentDateTime.minusDays(1)),
            RefreshToken.create(2L, "token2", currentDateTime.minusDays(2))
        );
        
        // Mocking: 만료된 토큰 조회 모킹
        when(findRefreshTokenPort.findExpiredTokens(currentDateTime)).thenReturn(expectedTokens);
        
        // when
        List<RefreshToken> actualTokens = findRefreshTokenService.findExpiredTokens(query);
        
        // then
        assertThat(actualTokens).hasSize(2);
        assertThat(actualTokens).containsExactlyElementsOf(expectedTokens);
    }

    @Test
    @DisplayName("쿼리가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_query_is_null() {
        assertThatThrownBy(() -> findRefreshTokenService.findByUserId(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }
}
