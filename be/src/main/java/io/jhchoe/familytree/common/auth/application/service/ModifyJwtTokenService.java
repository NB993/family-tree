package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.DeleteJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.DeleteRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.ModifyJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.ModifyJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.config.JwtProperties;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JWT 토큰 수정 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ModifyJwtTokenService implements ModifyJwtTokenUseCase {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;
    private final FindUserPort findUserPort;
    private final SaveRefreshTokenUseCase saveRefreshTokenUseCase;
    private final DeleteJwtTokenUseCase deleteJwtTokenUseCase;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public JwtTokenResponse modify(final ModifyJwtTokenCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. Refresh Token 검증 (JwtTokenUtil에서 FTException 발생)
        jwtTokenUtil.validateToken(command.refreshToken());

        // 2. 토큰에서 사용자 정보 추출
        Long userId = jwtTokenUtil.extractUserId(command.refreshToken());
        String email = jwtTokenUtil.extractEmail(command.refreshToken());
        String name = jwtTokenUtil.extractName(command.refreshToken());

        // 3. 기존 Refresh Token 무효화 (토큰 재사용 방지)
        deleteJwtTokenUseCase.delete(new DeleteRefreshTokenCommand(userId));

        // 4. 토큰에서 사용자 ID를 추출하여 User 조회 후 FTUser로 변환
        User user = findUserPort.findById(userId)
            .orElseThrow(() -> FTException.NOT_FOUND);

        FTUser ftUser = FTUser.withId(user.getId(), user.getEmail(), user.getName());

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
