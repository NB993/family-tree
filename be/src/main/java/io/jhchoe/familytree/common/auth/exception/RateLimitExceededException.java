package io.jhchoe.familytree.common.auth.exception;

/**
 * 요청 횟수 제한을 초과했을 때 발생하는 예외입니다.
 */
public class RateLimitExceededException extends RuntimeException {

    /**
     * 기본 메시지로 RateLimitExceededException을 생성합니다.
     */
    public RateLimitExceededException() {
        super("요청 횟수 제한을 초과했습니다");
    }

    /**
     * 지정된 메시지로 RateLimitExceededException을 생성합니다.
     *
     * @param message 예외 메시지
     */
    public RateLimitExceededException(final String message) {
        super(message);
    }

    /**
     * 지정된 메시지와 원인으로 RateLimitExceededException을 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public RateLimitExceededException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
