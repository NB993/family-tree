package io.jhchoe.familytree.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonException implements BaseExceptionType {

    MISSING_PARAMETER("C001", "파라미터 누락.", HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER("C002", "유효하지 않은 파라미터.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("C003", "대상을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    DUPLICATED("C004", "중복된 정보입니다.", HttpStatus.CONFLICT),
    ENUM_CONVERT("C005", "유효하지 않은 파라미터.", HttpStatus.BAD_REQUEST)
    ;

    String code;
    String message;
    HttpStatus status;

    CommonException(
        String code,
        String message,
        HttpStatus status
    ) {
        this.code = code;
        this.message = message;
        this.status = status;
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
