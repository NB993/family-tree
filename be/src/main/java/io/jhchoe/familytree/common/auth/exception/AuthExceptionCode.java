package io.jhchoe.familytree.common.auth.exception;

import io.jhchoe.familytree.common.exception.ExceptionCodeType;
import java.util.Arrays;
import org.springframework.http.HttpStatus;

public enum AuthExceptionCode implements ExceptionCodeType {

    UNAUTHORIZED("A001", "인증 실패.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("A002", "인증 실패.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("A003", "권한이 없습니다.", HttpStatus.FORBIDDEN),
    
    // JWT 토큰 관련 예외 코드
    EXPIRED_TOKEN("A004", "토큰이 만료되었습니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_FORMAT("A005", "유효하지 않은 토큰 형식입니다.", HttpStatus.UNAUTHORIZED),
    RATE_LIMIT_EXCEEDED("A007", "요청 횟수가 제한을 초과했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS);

    String code;
    String message;
    HttpStatus status;

    AuthExceptionCode(String code, String message, HttpStatus status) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    public static AuthExceptionCode ofCode(String code) {
        return Arrays.stream(AuthExceptionCode.values())
            .filter(authExceptionCode -> authExceptionCode.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }
}
