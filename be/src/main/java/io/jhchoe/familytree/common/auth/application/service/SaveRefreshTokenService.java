package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.out.SaveRefreshTokenPort;
import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Refresh Token 저장 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class SaveRefreshTokenService implements SaveRefreshTokenUseCase {

    private final SaveRefreshTokenPort saveRefreshTokenPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(SaveRefreshTokenCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 도메인 객체 생성
        RefreshToken refreshToken = RefreshToken.create(
            command.getUserId(),
            command.getTokenHash(),
            command.getExpiresAt()
        );

        // 저장 및 ID 반환
        return saveRefreshTokenPort.save(refreshToken);
    }
}
