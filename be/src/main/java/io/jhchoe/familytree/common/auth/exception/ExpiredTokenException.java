package io.jhchoe.familytree.common.auth.exception;

/**
 * JWT 토큰이 만료되었을 때 발생하는 예외입니다.
 */
public class ExpiredTokenException extends RuntimeException {

    /**
     * 기본 메시지로 ExpiredTokenException을 생성합니다.
     */
    public ExpiredTokenException() {
        super("세션이 만료되었습니다. 다시 로그인해주세요");
    }

    /**
     * 지정된 메시지로 ExpiredTokenException을 생성합니다.
     *
     * @param message 예외 메시지
     */
    public ExpiredTokenException(final String message) {
        super(message);
    }

    /**
     * 지정된 메시지와 원인으로 ExpiredTokenException을 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public ExpiredTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
