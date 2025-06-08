package io.jhchoe.familytree.common.auth.exception;

/**
 * JWT 토큰이 유효하지 않을 때 발생하는 예외입니다.
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * 기본 메시지로 InvalidTokenException을 생성합니다.
     */
    public InvalidTokenException() {
        super("유효하지 않은 토큰입니다");
    }

    /**
     * 지정된 메시지로 InvalidTokenException을 생성합니다.
     *
     * @param message 예외 메시지
     */
    public InvalidTokenException(final String message) {
        super(message);
    }

    /**
     * 지정된 메시지와 원인으로 InvalidTokenException을 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public InvalidTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
