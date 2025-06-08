package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.SaveRefreshTokenUseCase;
import io.jhchoe.familytree.common.auth.config.JwtProperties;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * JWT 토큰 생성 서비스
 * OAuth2 로그인 성공 시 Access Token과 Refresh Token을 생성하는 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class GenerateJwtTokenService implements GenerateJwtTokenUseCase {

    private final JwtTokenUtil jwtTokenUtil;
    private final SaveRefreshTokenUseCase saveRefreshTokenUseCase;
    private final JwtProperties jwtProperties;

    /**
     * 사용자 정보를 기반으로 JWT 토큰을 생성합니다.
     *
     * 1. Access Token 생성 (1시간 만료)
     * 2. Refresh Token 생성 (7일 만료)
     * 3. Refresh Token을 데이터베이스에 저장
     * 4. 토큰 응답 객체 반환
     *
     * @param command 토큰 생성에 필요한 사용자 정보
     * @return 생성된 JWT 토큰 정보
     */
    @Override
    @Transactional
    public JwtTokenResponse generateToken(final GenerateJwtTokenCommand command) {
        // Access Token 생성 (1시간 만료)
        final String accessToken = jwtTokenUtil.generateAccessToken(command.user());
        
        // Refresh Token 생성 (7일 만료)
        final String refreshToken = jwtTokenUtil.generateRefreshToken(command.getUserId());
        
        // Refresh Token을 데이터베이스에 저장 (기존 토큰이 있으면 덮어쓰기)
        final SaveRefreshTokenCommand saveCommand = new SaveRefreshTokenCommand(
                command.getUserId(),
                refreshToken,
                LocalDateTime.now().plusDays(7) // 7일 후 만료
        );
        saveRefreshTokenUseCase.save(saveCommand);
        
        // 토큰 응답 객체 반환 (expiresIn은 초 단위)
        return JwtTokenResponse.of(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpiration() // 1시간 = 3600초
        );
    }
}
