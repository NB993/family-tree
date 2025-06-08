package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.RefreshJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.RefreshJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.config.JwtProperties;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.exception.InvalidTokenException;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JWT 토큰 갱신 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class RefreshJwtTokenService implements RefreshJwtTokenUseCase {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;
    private final SaveRefreshTokenUseCase saveRefreshTokenUseCase;
    private final DeleteRefreshTokenUseCase deleteRefreshTokenUseCase;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public JwtTokenResponse refresh(final RefreshJwtTokenCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. Refresh Token 검증
        if (!jwtTokenUtil.validateToken(command.refreshToken())) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token입니다");
        }

        // 2. 토큰에서 사용자 ID 추출
        Long userId = jwtTokenUtil.extractUserId(command.refreshToken());

        // 3. 기존 Refresh Token 무효화 (토큰 재사용 방지)
        deleteRefreshTokenUseCase.delete(new DeleteRefreshTokenCommand(userId));

        // 4. 토큰에서 사용자 정보 추출하여 FTUser 생성
        String email = jwtTokenUtil.extractEmail(command.refreshToken());
        String name = jwtTokenUtil.extractName(command.refreshToken());
        FTUser ftUser = FTUser.withId(userId, email, name);

        // 5. 새로운 Access Token과 Refresh Token 생성
        String newAccessToken = jwtTokenUtil.generateAccessToken(ftUser);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(userId);

        // 6. 새로운 Refresh Token을 DB에 저장
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration());
        saveRefreshTokenUseCase.save(new SaveRefreshTokenCommand(userId, newRefreshToken, expiresAt));

        // 7. 토큰 응답 생성
        return new JwtTokenResponse(
            newAccessToken,
            newRefreshToken,
            "Bearer",
            jwtProperties.getAccessTokenExpiration()
        );
    }
}
