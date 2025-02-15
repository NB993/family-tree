package io.jhchoe.familytree.common.auth.exception;

import io.jhchoe.familytree.common.exception.ExceptionCodeType;
import org.springframework.http.HttpStatus;

public enum AuthExceptionCode implements ExceptionCodeType {

    UNAUTHORIZED("A001", "인증 실패.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("A002", "인증 실패.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("A003", "권한이 없습니다.", HttpStatus.FORBIDDEN);

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
}
