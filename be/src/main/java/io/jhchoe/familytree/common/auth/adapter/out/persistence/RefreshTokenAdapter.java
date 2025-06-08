package io.jhchoe.familytree.common.auth.adapter.out.persistence;

import io.jhchoe.familytree.common.auth.application.port.out.DeleteRefreshTokenPort;
import io.jhchoe.familytree.common.auth.application.port.out.FindRefreshTokenPort;
import io.jhchoe.familytree.common.auth.application.port.out.SaveRefreshTokenPort;
import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * RefreshToken 관련 아웃바운드 포트를 구현하는 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenAdapter implements FindRefreshTokenPort, SaveRefreshTokenPort, DeleteRefreshTokenPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        
        return refreshTokenJpaRepository.findByUserId(userId)
            .map(RefreshTokenJpaEntity::toRefreshToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RefreshToken> findExpiredTokens(LocalDateTime currentDateTime) {
        Objects.requireNonNull(currentDateTime, "currentDateTime must not be null");
        
        return refreshTokenJpaRepository.findExpiredTokens(currentDateTime)
            .stream()
            .map(RefreshTokenJpaEntity::toRefreshToken)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(RefreshToken refreshToken) {
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
        
        // 기존 토큰이 있다면 삭제 후 새로 저장 (Upsert 동작)
        if (refreshToken.getId() == null && refreshTokenJpaRepository.existsByUserId(refreshToken.getUserId())) {
            refreshTokenJpaRepository.deleteByUserId(refreshToken.getUserId());
            refreshTokenJpaRepository.flush(); // 즉시 삭제 반영
        }
        
        RefreshTokenJpaEntity entity = RefreshTokenJpaEntity.from(refreshToken);
        RefreshTokenJpaEntity savedEntity = refreshTokenJpaRepository.save(entity);
        
        return savedEntity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByUserId(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        
        refreshTokenJpaRepository.deleteByUserId(userId);
    }
}
