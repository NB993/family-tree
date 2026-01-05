package io.jhchoe.familytree.common.auth.filter;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.auth.util.JwtTokenUtil;
import io.jhchoe.familytree.common.exception.FTException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] JwtAuthenticationFilterTest")
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 Bearer 토큰이 있을 때 인증 정보를 설정합니다")
    void set_authentication_when_valid_bearer_token() throws ServletException, IOException {
        // given
        String validToken = "valid.jwt.token";
        Long userId = 1L;
        String email = "test@example.com";
        String name = "테스트 사용자";
        String role = "USER";

        // HTTP 요청에 Authorization 헤더 설정
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // JWT 토큰 검증 및 정보 추출 모킹
        when(jwtTokenUtil.validateToken(validToken)).thenReturn(true);
        when(jwtTokenUtil.extractUserId(validToken)).thenReturn(userId);
        when(jwtTokenUtil.extractEmail(validToken)).thenReturn(email);
        when(jwtTokenUtil.extractName(validToken)).thenReturn(name);
        when(jwtTokenUtil.extractRole(validToken)).thenReturn(role);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isInstanceOf(FTUser.class);

        FTUser ftUser = (FTUser) authentication.getPrincipal();
        assertThat(ftUser.getId()).isEqualTo(userId);
        assertThat(ftUser.getEmail()).isEqualTo(email);
        assertThat(ftUser.getName()).isEqualTo(name);
        assertThat(ftUser.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_USER");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더가 없을 때 인증 정보를 설정하지 않습니다")
    void not_set_authentication_when_no_authorization_header() throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Bearer 접두사가 없을 때 인증 정보를 설정하지 않습니다")
    void not_set_authentication_when_no_bearer_prefix() throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("만료된 토큰일 때 요청 속성에 예외 정보를 설정합니다")
    void set_exception_attribute_when_expired_token() throws ServletException, IOException {
        // given
        String expiredToken = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);

        // 만료된 토큰 예외 모킹
        when(jwtTokenUtil.validateToken(expiredToken)).thenThrow(new FTException(AuthExceptionCode.EXPIRED_TOKEN));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        // SecurityContext가 비워졌는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        // 예외 정보가 요청 속성에 설정되었는지 확인
        verify(request).setAttribute("JWT_EXCEPTION", AuthExceptionCode.EXPIRED_TOKEN.name());

        // 필터 체인은 계속 진행됨
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 때 요청 속성에 예외 정보를 설정합니다")
    void set_exception_attribute_when_invalid_token() throws ServletException, IOException {
        // given
        String invalidToken = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);

        // 유효하지 않은 토큰 예외 모킹
        when(jwtTokenUtil.validateToken(invalidToken)).thenThrow(new FTException(AuthExceptionCode.INVALID_TOKEN_FORMAT));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        // SecurityContext가 비워졌는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        // 예외 정보가 요청 속성에 설정되었는지 확인
        verify(request).setAttribute("JWT_EXCEPTION", AuthExceptionCode.INVALID_TOKEN_FORMAT.name());

        // 필터 체인은 계속 진행됨
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("이미 인증된 상태일 때 토큰 처리를 건너뜁니다")
    void skip_token_processing_when_already_authenticated() throws ServletException, IOException {
        // given
        String validToken = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // 이미 인증된 상태로 설정
        FTUser existingUser = FTUser.ofJwtUser(1L, "기존 사용자", "existing@example.com", "USER");
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                existingUser, null, existingUser.getAuthorities()
            )
        );

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(jwtTokenUtil, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("공개 엔드포인트에 대해서는 필터를 적용하지 않습니다")
    void should_not_filter_public_endpoints() {
        // given & when & then
        assertThat(jwtAuthenticationFilter.shouldNotFilter(createRequestWithPath("/favicon.ico"))).isTrue();
        assertThat(jwtAuthenticationFilter.shouldNotFilter(createRequestWithPath("/docs/index.html"))).isTrue();
        assertThat(jwtAuthenticationFilter.shouldNotFilter(createRequestWithPath("/h2-console/login"))).isTrue();
        assertThat(jwtAuthenticationFilter.shouldNotFilter(createRequestWithPath("/signup"))).isTrue();
        assertThat(jwtAuthenticationFilter.shouldNotFilter(createRequestWithPath("/"))).isTrue();
        assertThat(jwtAuthenticationFilter.shouldNotFilter(createRequestWithPath("/login"))).isTrue();
        assertThat(jwtAuthenticationFilter.shouldNotFilter(createRequestWithPath("/logout"))).isTrue();

        // API 엔드포인트는 필터를 적용해야 함
        assertThat(jwtAuthenticationFilter.shouldNotFilter(createRequestWithPath("/api/families"))).isFalse();
    }

    @Test
    @DisplayName("토큰에서 사용자 정보 추출 중 예외가 발생할 때 요청 속성에 예외 정보를 설정합니다")
    void set_exception_attribute_when_exception_during_token_processing() throws ServletException, IOException {
        // given
        String validToken = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // 토큰 검증은 성공하지만 사용자 정보 추출에서 예외 발생
        when(jwtTokenUtil.validateToken(validToken)).thenReturn(true);
        when(jwtTokenUtil.extractUserId(validToken)).thenThrow(new RuntimeException("예상치 못한 오류"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        // SecurityContext가 비워졌는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        // 예외 정보가 요청 속성에 설정되었는지 확인
        verify(request).setAttribute("JWT_EXCEPTION", "UNAUTHORIZED");

        // 필터 체인은 계속 진행됨
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("ADMIN 역할을 가진 JWT 토큰으로 인증 시 ADMIN 권한을 가집니다")
    void set_admin_authority_when_admin_jwt_token() throws ServletException, IOException {
        // given
        String adminToken = "admin.jwt.token";
        Long userId = 1L;
        String email = "admin@example.com";
        String name = "관리자";
        String role = "ADMIN";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + adminToken);
        when(jwtTokenUtil.validateToken(adminToken)).thenReturn(true);
        when(jwtTokenUtil.extractUserId(adminToken)).thenReturn(userId);
        when(jwtTokenUtil.extractEmail(adminToken)).thenReturn(email);
        when(jwtTokenUtil.extractName(adminToken)).thenReturn(name);
        when(jwtTokenUtil.extractRole(adminToken)).thenReturn(role);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();

        FTUser ftUser = (FTUser) authentication.getPrincipal();
        assertThat(ftUser.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_ADMIN");

        verify(filterChain).doFilter(request, response);
    }

    /**
     * 테스트용 HttpServletRequest 모의 객체를 생성합니다.
     */
    private HttpServletRequest createRequestWithPath(String path) {
        HttpServletRequest testRequest = mock(HttpServletRequest.class);
        when(testRequest.getRequestURI()).thenReturn(path);
        return testRequest;
    }
}
