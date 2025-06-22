package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.out.DeleteRefreshTokenPort;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JWT 토큰 삭제 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class DeleteJwtTokenService implements DeleteJwtTokenUseCase {

    private final DeleteRefreshTokenPort deleteRefreshTokenPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(final DeleteRefreshTokenCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 해당 사용자의 모든 Refresh Token을 무효화
        deleteRefreshTokenPort.deleteByUserId(command.getUserId());
    }
}
