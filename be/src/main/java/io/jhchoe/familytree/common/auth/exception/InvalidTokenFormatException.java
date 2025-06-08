package io.jhchoe.familytree.common.auth.exception;

/**
 * JWT 토큰 형식이 올바르지 않을 때 발생하는 예외입니다.
 */
public class InvalidTokenFormatException extends RuntimeException {

    /**
     * 기본 메시지로 InvalidTokenFormatException을 생성합니다.
     */
    public InvalidTokenFormatException() {
        super("토큰 형식이 올바르지 않습니다");
    }

    /**
     * 지정된 메시지로 InvalidTokenFormatException을 생성합니다.
     *
     * @param message 예외 메시지
     */
    public InvalidTokenFormatException(final String message) {
        super(message);
    }

    /**
     * 지정된 메시지와 원인으로 InvalidTokenFormatException을 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public InvalidTokenFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
