package io.jhchoe.familytree.core.family.exception;

import io.jhchoe.familytree.common.exception.ExceptionCodeType;
import org.springframework.http.HttpStatus;

/**
 * Family 도메인에서 발생하는 예외의 코드를 정의하는 열거형입니다.
 */
public enum FamilyExceptionCode implements ExceptionCodeType {

    NOT_FAMILY_MEMBER("F001", "가족 구성원만 접근할 수 있습니다.", HttpStatus.FORBIDDEN),
    FAMILY_NOT_FOUND("F002", "존재하지 않는 패밀리입니다.", HttpStatus.NOT_FOUND),
    ALREADY_JOINED_FAMILY("F003", "이미 가입된 패밀리입니다.", HttpStatus.CONFLICT),
    JOIN_REQUEST_ALREADY_PENDING("F004", "가입 신청을 처리중이에요.", HttpStatus.CONFLICT),
    EXCEEDED_FAMILY_JOIN_LIMIT("F005", "최대 패밀리 가입 수를 초과했습니다.", HttpStatus.BAD_REQUEST),
    JOIN_REQUEST_REJECTED("F006", "가입 신청이 불가능합니다.", HttpStatus.FORBIDDEN),
    MEMBER_NOT_FOUND("F007", "해당 구성원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_AUTHORIZED("F008", "해당 작업을 수행할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    MEMBER_NOT_ACTIVE("F009", "활성 상태가 아닌 구성원입니다.", HttpStatus.FORBIDDEN),
    CANNOT_CHANGE_OWNER_ROLE("F010", "Family 소유자의 역할은 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_CHANGE_OWNER_STATUS("F011", "Family 소유자의 상태는 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    SELF_MODIFICATION_NOT_ALLOWED("F012", "자신의 역할이나 상태는 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ADMIN_MODIFICATION_NOT_ALLOWED("F013", "관리자는 다른 관리자의 상태를 변경할 수 없습니다.", HttpStatus.FORBIDDEN),
    ANNOUNCEMENT_NOT_FOUND("F014", "존재하지 않는 공지사항입니다.", HttpStatus.NOT_FOUND),
    INVALID_ANNOUNCEMENT_REQUEST("F015", "잘못된 공지사항 요청입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    FamilyExceptionCode(
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
