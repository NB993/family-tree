package io.jhchoe.familytree.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.exception.ErrorResponse;
import io.jhchoe.familytree.common.exception.FTException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

/**
 * FTSpringSecurityExceptionHandler의 단위 테스트입니다.
 * JWT 예외 처리 로직과 .name() → valueOf() 패턴을 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FTSpringSecurityExceptionHandler")
class FTSpringSecurityExceptionHandlerTest {

    @InjectMocks
    private FTSpringSecurityExceptionHandler handler;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        given(response.getWriter()).willReturn(printWriter);
        willDoNothing().given(response).setStatus(anyInt());
        willDoNothing().given(response).setContentType(anyString());
        willDoNothing().given(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("JWT 필터에서 설정한 EXPIRED_TOKEN 속성을 우선적으로 처리합니다")
    void handle_jwt_exception_attribute_expired_token() throws Exception {
        // given
        given(request.getAttribute("JWT_EXCEPTION")).willReturn(AuthExceptionCode.EXPIRED_TOKEN.name());
        AuthenticationException authException = new BadCredentialsException("Full authentication is required");

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.EXPIRED_TOKEN.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("JWT 필터에서 설정한 INVALID_TOKEN_FORMAT 속성을 우선적으로 처리합니다")
    void handle_jwt_exception_attribute_invalid_token_format() throws Exception {
        // given
        given(request.getAttribute("JWT_EXCEPTION")).willReturn(AuthExceptionCode.INVALID_TOKEN_FORMAT.name());
        AuthenticationException authException = new BadCredentialsException("Full authentication is required");

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.INVALID_TOKEN_FORMAT.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("JWT_EXCEPTION 속성이 없으면 메시지에서 예외 코드를 찾습니다")
    void fallback_to_message_when_no_jwt_exception_attribute() throws Exception {
        // given
        given(request.getAttribute("JWT_EXCEPTION")).willReturn(null);
        String exceptionCodeName = AuthExceptionCode.UNAUTHORIZED.name();
        AuthenticationException authException = new BadCredentialsException(exceptionCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("JWT_EXCEPTION 속성이 잘못된 값이면 메시지에서 예외 코드를 찾습니다")
    void fallback_to_message_when_invalid_jwt_exception_attribute() throws Exception {
        // given
        given(request.getAttribute("JWT_EXCEPTION")).willReturn("INVALID_CODE");
        String exceptionCodeName = AuthExceptionCode.UNAUTHORIZED.name();
        AuthenticationException authException = new BadCredentialsException(exceptionCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("유효한 INVALID_TOKEN_FORMAT 예외 코드를 올바르게 처리합니다")
    void handle_invalid_token_format_exception_code() throws Exception {
        // given
        String exceptionCodeName = AuthExceptionCode.INVALID_TOKEN_FORMAT.name();
        AuthenticationException authException = new BadCredentialsException(exceptionCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.INVALID_TOKEN_FORMAT.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("유효한 RATE_LIMIT_EXCEEDED 예외 코드를 올바르게 처리합니다")
    void handle_rate_limit_exceeded_exception_code() throws Exception {
        // given
        String exceptionCodeName = AuthExceptionCode.RATE_LIMIT_EXCEEDED.name();
        AuthenticationException authException = new BadCredentialsException(exceptionCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.RATE_LIMIT_EXCEEDED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("유효한 USER_NOT_FOUND 예외 코드를 올바르게 처리합니다")
    void handle_user_not_found_exception_code() throws Exception {
        // given
        String exceptionCodeName = AuthExceptionCode.USER_NOT_FOUND.name();
        AuthenticationException authException = new BadCredentialsException(exceptionCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.USER_NOT_FOUND.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("유효한 UNAUTHORIZED 예외 코드를 올바르게 처리합니다")
    void handle_unauthorized_exception_code() throws Exception {
        // given
        String exceptionCodeName = AuthExceptionCode.UNAUTHORIZED.name();
        AuthenticationException authException = new BadCredentialsException(exceptionCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("null 메시지가 전달되면 UNAUTHORIZED로 기본 처리합니다")
    void handle_null_message_with_default_unauthorized() throws Exception {
        // given
        AuthenticationException authException = new BadCredentialsException(null);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("잘못된 예외 코드 이름이 전달되면 UNAUTHORIZED로 기본 처리합니다")
    void handle_invalid_exception_code_name_with_default_unauthorized() throws Exception {
        // given
        String invalidCodeName = "INVALID_EXCEPTION_CODE";
        AuthenticationException authException = new BadCredentialsException(invalidCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("빈 문자열 메시지가 전달되면 valueOf 실패로 UNAUTHORIZED로 기본 처리합니다")
    void handle_empty_message_with_default_unauthorized() throws Exception {
        // given
        AuthenticationException authException = new BadCredentialsException("");

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("공백 문자열 메시지가 전달되면 valueOf 실패로 UNAUTHORIZED로 기본 처리합니다")
    void handle_whitespace_message_with_default_unauthorized() throws Exception {
        // given
        AuthenticationException authException = new BadCredentialsException("   ");

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("AccessDeniedException을 올바르게 처리합니다")
    void handle_access_denied_exception() throws Exception {
        // given
        AccessDeniedException accessDeniedException = new AccessDeniedException("접근 거부");

        // when
        handler.handle(request, response, accessDeniedException);

        // then
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("대소문자가 다른 예외 코드 이름은 UNAUTHORIZED로 처리합니다")
    void handle_case_sensitive_exception_code_name() throws Exception {
        // given
        String lowercaseCodeName = AuthExceptionCode.EXPIRED_TOKEN.name().toLowerCase();
        AuthenticationException authException = new BadCredentialsException(lowercaseCodeName);

        // when
        handler.commence(request, response, authException);

        // then - valueOf는 대소문자를 구분하므로 기본값으로 처리됨
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("예외 코드 이름에 공백이 포함된 경우 UNAUTHORIZED로 처리합니다")
    void handle_exception_code_name_with_spaces() throws Exception {
        // given
        String codeNameWithSpaces = " " + AuthExceptionCode.EXPIRED_TOKEN.name() + " ";
        AuthenticationException authException = new BadCredentialsException(codeNameWithSpaces);

        // when
        handler.commence(request, response, authException);

        // then - 공백이 포함되면 valueOf가 실패하므로 기본값으로 처리됨
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("숫자나 특수문자가 포함된 잘못된 예외 코드는 UNAUTHORIZED로 처리합니다")
    void handle_invalid_exception_code_with_numbers_and_symbols() throws Exception {
        // given
        String invalidCodeName = "INVALID_CODE_123!@#";
        AuthenticationException authException = new BadCredentialsException(invalidCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("매우 긴 잘못된 예외 코드 이름도 UNAUTHORIZED로 처리합니다")
    void handle_very_long_invalid_exception_code_name() throws Exception {
        // given
        String veryLongInvalidCodeName = "A".repeat(1000) + "_INVALID_CODE";
        AuthenticationException authException = new BadCredentialsException(veryLongInvalidCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("예외 코드 이름이 부분적으로 일치하는 경우도 UNAUTHORIZED로 처리합니다")
    void handle_partially_matching_exception_code_name() throws Exception {
        // given
        String partialCodeName = "EXPIRED"; // EXPIRED_TOKEN의 일부
        AuthenticationException authException = new BadCredentialsException(partialCodeName);

        // when
        handler.commence(request, response, authException);

        // then
        verify(response).setStatus(AuthExceptionCode.UNAUTHORIZED.getStatus().value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(any(PrintWriter.class), any(ErrorResponse.class));
    }

    @Test
    @DisplayName("valueOf 패턴이 정확히 동작하는지 직접 검증합니다")
    void verify_value_of_pattern_works_correctly() {
        // 이 테스트는 Mock을 사용하지 않으므로 setUp()의 Mock stubbing을 재설정
        org.mockito.Mockito.reset(objectMapper, response);
        
        // given & when & then - 모든 AuthExceptionCode가 valueOf로 변환 가능한지 확인
        for (AuthExceptionCode exceptionCode : AuthExceptionCode.values()) {
            String name = exceptionCode.name();
            AuthExceptionCode converted = AuthExceptionCode.valueOf(name);
            
            assertThat(converted).isEqualTo(exceptionCode);
            assertThat(converted.getCode()).isEqualTo(exceptionCode.getCode());
            assertThat(converted.getMessage()).isEqualTo(exceptionCode.getMessage());
            assertThat(converted.getStatus()).isEqualTo(exceptionCode.getStatus());
        }
    }

    @Test
    @DisplayName("잘못된 문자열로 valueOf 호출 시 IllegalArgumentException이 발생합니다")
    void verify_value_of_throws_exception_for_invalid_strings() {
        // 이 테스트는 Mock을 사용하지 않으므로 setUp()의 Mock stubbing을 재설정
        org.mockito.Mockito.reset(objectMapper, response);
        
        // given
        String[] invalidNames = {
            "invalid_code",
            "EXPIRED_TOKEN_WRONG",
            "123",
            "",
            "   ",
            "expired_token", // 소문자
            "Expired_Token", // 카멜케이스
        };

        // when & then
        for (String invalidName : invalidNames) {
            try {
                AuthExceptionCode.valueOf(invalidName);
                // valueOf가 성공하면 안 되는 경우
                if (!isValidExceptionCodeName(invalidName)) {
                    throw new AssertionError("Expected IllegalArgumentException for: " + invalidName);
                }
            } catch (IllegalArgumentException e) {
                // 예상된 예외 - 정상
                assertThat(e).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    /**
     * 주어진 문자열이 유효한 AuthExceptionCode 이름인지 확인합니다.
     */
    private boolean isValidExceptionCodeName(String name) {
        for (AuthExceptionCode code : AuthExceptionCode.values()) {
            if (code.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
