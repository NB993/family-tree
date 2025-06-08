package io.jhchoe.familytree.common.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.common.auth.application.port.in.LogoutCommand;
import io.jhchoe.familytree.common.auth.application.port.in.LogoutUseCase;
import io.jhchoe.familytree.common.auth.application.port.in.RefreshJwtTokenCommand;
import io.jhchoe.familytree.common.auth.application.port.in.RefreshJwtTokenUseCase;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.dto.JwtTokenResponse;
import io.jhchoe.familytree.common.auth.dto.TokenRefreshRequest;
import io.jhchoe.familytree.common.auth.exception.ExpiredTokenException;
import io.jhchoe.familytree.common.auth.exception.InvalidTokenException;
import io.jhchoe.familytree.config.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TokenController 단위 테스트
 */
@WebMvcTest(TokenController.class)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RefreshJwtTokenUseCase refreshJwtTokenUseCase;

    @MockBean
    private LogoutUseCase logoutUseCase;

    @Test
    @DisplayName("유효한 Refresh Token으로 토큰 갱신 성공")
    void refresh_token_with_valid_refresh_token_should_return_new_tokens() throws Exception {
        // given
        String refreshToken = "valid.refresh.token";
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);
        
        JwtTokenResponse expectedResponse = JwtTokenResponse.of(
            "new.access.token",
            "new.refresh.token",
            300L
        );

        when(refreshJwtTokenUseCase.refresh(any(RefreshJwtTokenCommand.class)))
            .thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").value("new.access.token"))
            .andExpect(jsonPath("$.refreshToken").value("new.refresh.token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(300));
    }

    @Test
    @DisplayName("빈 Refresh Token으로 토큰 갱신 시 400 에러")
    void refresh_token_with_empty_refresh_token_should_return_400() throws Exception {
        // given
        TokenRefreshRequest request = new TokenRefreshRequest("");

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    @DisplayName("만료된 Refresh Token으로 토큰 갱신 시 예외 발생")
    void refresh_token_with_expired_token_should_throw_exception() throws Exception {
        // given
        String expiredToken = "expired.refresh.token";
        TokenRefreshRequest request = new TokenRefreshRequest(expiredToken);

        when(refreshJwtTokenUseCase.refresh(any(RefreshJwtTokenCommand.class)))
            .thenThrow(new ExpiredTokenException("Refresh Token이 만료되었습니다."));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효하지 않은 Refresh Token으로 토큰 갱신 시 예외 발생")
    void refresh_token_with_invalid_token_should_throw_exception() throws Exception {
        // given
        String invalidToken = "invalid.refresh.token";
        TokenRefreshRequest request = new TokenRefreshRequest(invalidToken);

        when(refreshJwtTokenUseCase.refresh(any(RefreshJwtTokenCommand.class)))
            .thenThrow(new InvalidTokenException("유효하지 않은 Refresh Token입니다."));

        // when & then
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockOAuth2User(id = 1L, email = "test@example.com", name = "테스트")
    @DisplayName("인증된 사용자의 로그아웃 성공")
    void logout_with_authenticated_user_should_return_success() throws Exception {
        // given
        doNothing().when(logoutUseCase).logout(any(LogoutCommand.class));

        // when & then
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("로그아웃이 성공적으로 완료되었습니다."));
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 로그아웃 시 401 에러")
    void logout_without_authentication_should_return_401() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isUnauthorized());
    }
}
