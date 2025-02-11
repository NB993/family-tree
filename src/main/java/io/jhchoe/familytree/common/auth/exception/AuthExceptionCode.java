package io.jhchoe.familytree.common.auth.exception;

import io.jhchoe.familytree.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthExceptionCode implements BaseExceptionType {

    USER_NOT_FOUND("A001", "인증 실패.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("A002", "권한이 없습니다.", HttpStatus.FORBIDDEN),
    ;

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
        return "";
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public HttpStatus getStatus() {
        return null;
    }
}
