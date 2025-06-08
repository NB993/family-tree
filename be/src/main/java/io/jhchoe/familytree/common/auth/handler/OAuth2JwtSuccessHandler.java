package io.jhchoe.familytree.common.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.GenerateJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 발급하는 핸들러
 * 
 * 기존 세션 기반 인증 대신 JWT 토큰을 발급하여 프론트엔드에 응답합니다.
 * 이를 통해 Stateless 아키텍처를 구현하고 SPA 개발을 지원합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2JwtSuccessHandler implements AuthenticationSuccessHandler {

    private final GenerateJwtTokenUseCase generateJwtTokenUseCase;
    private final ObjectMapper objectMapper;

    /**
     * OAuth2 인증 성공 시 JWT 토큰을 생성하고 JSON 응답으로 반환합니다.
     *
     * @param request        HTTP 요청
     * @param response       HTTP 응답
     * @param authentication 인증 정보 (FTUser 포함)
     * @throws IOException      JSON 응답 생성 실패 시
     * @throws ServletException 서블릿 처리 실패 시
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        log.info("OAuth2 인증 성공, JWT 토큰 발급 시작");
        
        try {
            // 인증된 사용자 정보 추출
            final FTUser ftUser = (FTUser) authentication.getPrincipal();
            log.info("JWT 토큰 발급 대상 사용자: [User ID: {}] [Email: {}]", 
                    ftUser.getId(), ftUser.getEmail());
            
            // JWT 토큰 생성
            final GenerateJwtTokenCommand command = new GenerateJwtTokenCommand(ftUser);
            final JwtTokenResponse tokenResponse = generateJwtTokenUseCase.generateToken(command);
            
            // JSON 응답 설정
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            
            // 성공 응답 생성
            final OAuth2JwtLoginResponse loginResponse = OAuth2JwtLoginResponse.success(
                    tokenResponse,
                    ftUser.getId(),
                    ftUser.getEmail(),
                    ftUser.getName()
            );
            
            // JSON 응답 작성
            final String jsonResponse = objectMapper.writeValueAsString(loginResponse);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
            
            log.info("OAuth2 JWT 토큰 발급 완료: [User ID: {}]", ftUser.getId());
            
        } catch (Exception e) {
            log.error("OAuth2 JWT 토큰 발급 중 오류 발생", e);
            handleTokenGenerationError(response, e);
        }
    }

    /**
     * 토큰 생성 오류 발생 시 에러 응답을 생성합니다.
     */
    private void handleTokenGenerationError(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        final OAuth2JwtLoginResponse errorResponse = OAuth2JwtLoginResponse.error(
                "토큰 생성 중 오류가 발생했습니다. 다시 시도해주세요."
        );
        
        final String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * OAuth2 JWT 로그인 응답 DTO
     */
    public record OAuth2JwtLoginResponse(
            boolean success,
            String message,
            JwtTokenResponse tokenInfo,
            UserInfo userInfo,
            String errorMessage
    ) {
        public static OAuth2JwtLoginResponse success(JwtTokenResponse tokenInfo, Long userId, String email, String name) {
            return new OAuth2JwtLoginResponse(
                    true,
                    "로그인이 성공적으로 완료되었습니다.",
                    tokenInfo,
                    new UserInfo(userId, email, name),
                    null
            );
        }
        
        public static OAuth2JwtLoginResponse error(String errorMessage) {
            return new OAuth2JwtLoginResponse(
                    false,
                    "로그인에 실패했습니다.",
                    null,
                    null,
                    errorMessage
            );
        }

        /**
         * 응답에 포함될 사용자 기본 정보
         */
        public record UserInfo(
                Long id,
                String email,
                String name
        ) {}
    }
}
