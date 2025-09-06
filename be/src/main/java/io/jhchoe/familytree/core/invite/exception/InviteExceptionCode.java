package io.jhchoe.familytree.core.invite.exception;

import io.jhchoe.familytree.common.exception.ExceptionCodeType;
import org.springframework.http.HttpStatus;

/**
 * Invite 도메인에서 발생하는 예외의 코드를 정의하는 열거형입니다.
 */
public enum InviteExceptionCode implements ExceptionCodeType {

    INVITE_NOT_FOUND("I001", "존재하지 않는 초대입니다.", HttpStatus.NOT_FOUND),
    INVITE_EXPIRED("I002", "초대가 만료되었습니다.", HttpStatus.BAD_REQUEST),
    INVITE_ALREADY_COMPLETED("I003", "이미 완료된 초대입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_RESPONDED("I004", "이미 응답한 초대입니다.", HttpStatus.CONFLICT),
    INVALID_INVITE_CODE("I005", "유효하지 않은 초대 코드입니다.", HttpStatus.BAD_REQUEST),
    NOT_AUTHORIZED_TO_CREATE_INVITE("I006", "초대를 생성할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    RESPONSE_NOT_FOUND("I007", "존재하지 않는 초대 응답입니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    InviteExceptionCode(
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