package io.jhchoe.familytree.common.auth.adapter.out.persistence;

import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("[Adapter Test] RefreshTokenAdapterTest")
class RefreshTokenAdapterTest extends TestcontainersDataJpaTestBase {

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;
    
    private RefreshTokenAdapter sut;
    
    @BeforeEach
    void setUp() {
        sut = new RefreshTokenAdapter(refreshTokenJpaRepository);
    }
    
    @Test
    @DisplayName("사용자 ID로 조회 시 RefreshToken을 반환합니다")
    void find_returns_refresh_token_when_user_id_exists() {
        // given
        Long userId = 1L;
        RefreshToken refreshToken = RefreshToken.newRefreshToken(
            userId, "hashed-token-value", LocalDateTime.now().plusDays(7)
        );
        RefreshTokenJpaEntity savedEntity = refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(refreshToken));
        
        // when
        Optional<RefreshToken> result = sut.findByUserId(userId);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getTokenHash()).isEqualTo("hashed-token-value");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 시 빈 Optional을 반환합니다")
    void find_returns_empty_when_user_id_not_exists() {
        // given
        Long nonExistentUserId = 999L;
        
        // when
        Optional<RefreshToken> result = sut.findByUserId(nonExistentUserId);
        
        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("만료된 토큰들을 조회 시 토큰 목록을 반환합니다")
    void find_expired_tokens_returns_expired_tokens_only() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.now();
        
        // 만료된 토큰들
        RefreshToken expiredToken1 = RefreshToken.newRefreshToken(
            1L, "expired-token-1", currentDateTime.minusDays(1)
        );
        RefreshToken expiredToken2 = RefreshToken.newRefreshToken(
            2L, "expired-token-2", currentDateTime.minusDays(2)
        );
        
        // 유효한 토큰
        RefreshToken validToken = RefreshToken.newRefreshToken(
            3L, "valid-token", currentDateTime.plusDays(1)
        );
        
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(expiredToken1));
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(expiredToken2));
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(validToken));
        
        // when
        List<RefreshToken> expiredTokens = sut.findExpiredTokens(currentDateTime);
        
        // then
        assertThat(expiredTokens).hasSize(2);
        assertThat(expiredTokens)
            .extracting(RefreshToken::getUserId)
            .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("토큰 저장 시 ID를 반환합니다")
    void save_returns_id_when_token_is_valid() {
        // given
        RefreshToken refreshToken = RefreshToken.newRefreshToken(
            1L, "hashed-token-value", LocalDateTime.now().plusDays(7)
        );
        
        // when
        Long savedId = sut.save(refreshToken);
        
        // then
        assertThat(savedId).isNotNull();
        
        // 저장된 토큰 확인
        Optional<RefreshTokenJpaEntity> savedEntity = refreshTokenJpaRepository.findById(savedId);
        assertThat(savedEntity).isPresent();
        assertThat(savedEntity.get().getUserId()).isEqualTo(1L);
        assertThat(savedEntity.get().getTokenHash()).isEqualTo("hashed-token-value");
    }

    @Test
    @DisplayName("기존 사용자 토큰이 있을 때 새 토큰 저장 시 기존 토큰이 교체됩니다")
    void save_replaces_existing_token_when_user_already_has_token() {
        // given
        Long userId = 1L;
        
        // 기존 토큰 저장
        RefreshToken oldToken = RefreshToken.newRefreshToken(
            userId, "old-token-hash", LocalDateTime.now().plusDays(7)
        );
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(oldToken));
        
        // 새 토큰
        RefreshToken newToken = RefreshToken.newRefreshToken(
            userId, "new-token-hash", LocalDateTime.now().plusDays(7)
        );
        
        // when
        Long savedId = sut.save(newToken);
        
        // then
        assertThat(savedId).isNotNull();
        
        // 사용자당 하나의 토큰만 존재하는지 확인
        List<RefreshTokenJpaEntity> userTokens = refreshTokenJpaRepository.findAll()
            .stream()
            .filter(token -> token.getUserId().equals(userId))
            .toList();
        
        assertThat(userTokens).hasSize(1);
        assertThat(userTokens.get(0).getTokenHash()).isEqualTo("new-token-hash");
    }

    @Test
    @DisplayName("사용자 ID로 토큰 삭제 시 해당 토큰이 삭제됩니다")
    void delete_removes_token_when_user_id_exists() {
        // given
        Long userId = 1L;
        RefreshToken refreshToken = RefreshToken.newRefreshToken(
            userId, "hashed-token-value", LocalDateTime.now().plusDays(7)
        );
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.from(refreshToken));
        
        // when
        sut.deleteByUserId(userId);
        
        // then
        Optional<RefreshTokenJpaEntity> deletedToken = refreshTokenJpaRepository.findByUserId(userId);
        assertThat(deletedToken).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 토큰 삭제 시 오류가 발생하지 않습니다")
    void delete_does_not_throw_exception_when_user_id_not_exists() {
        // given
        Long nonExistentUserId = 999L;
        
        // when & then
        assertThatCode(() -> sut.deleteByUserId(nonExistentUserId))
            .doesNotThrowAnyException();
    }
}
