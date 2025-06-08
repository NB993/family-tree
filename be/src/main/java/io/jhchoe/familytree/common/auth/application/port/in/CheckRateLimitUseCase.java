package io.jhchoe.familytree.common.auth.application.port.in;

/**
 * Rate Limiting 체크를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface CheckRateLimitUseCase {

    /**
     * Rate Limit 제한 여부를 체크합니다.
     *
     * @param command Rate Limit 체크에 필요한 입력 데이터를 포함하는 커맨드 객체
     * @return true: 요청 허용, false: Rate Limit 초과로 요청 거부
     */
    boolean checkRateLimit(CheckRateLimitCommand command);
}
