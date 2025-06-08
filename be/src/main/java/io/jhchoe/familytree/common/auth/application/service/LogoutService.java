package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.LogoutCommand;
import io.jhchoe.familytree.common.auth.application.port.in.LogoutUseCase;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그아웃 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    private final DeleteRefreshTokenUseCase deleteRefreshTokenUseCase;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void logout(final LogoutCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 해당 사용자의 모든 Refresh Token을 무효화
        deleteRefreshTokenUseCase.delete(new DeleteRefreshTokenCommand(command.userId()));
    }
}
