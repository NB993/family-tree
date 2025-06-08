package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.FindExpiredRefreshTokensQuery;
import io.jhchoe.familytree.common.auth.application.port.in.FindRefreshTokenByUserIdQuery;
import io.jhchoe.familytree.common.auth.application.port.in.FindRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.out.FindRefreshTokenPort;
import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Refresh Token 조회 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindRefreshTokenService implements FindRefreshTokenUseCase {

    private final FindRefreshTokenPort findRefreshTokenPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByUserId(FindRefreshTokenByUserIdQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findRefreshTokenPort.findByUserId(query.getUserId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findExpiredTokens(FindExpiredRefreshTokensQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findRefreshTokenPort.findExpiredTokens(query.getCurrentDateTime());
    }
}
