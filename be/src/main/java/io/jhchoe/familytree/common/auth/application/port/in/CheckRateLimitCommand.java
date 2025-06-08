package io.jhchoe.familytree.common.auth.application.port.in;

import java.util.Objects;

/**
 * Rate Limit 체크 요청을 나타내는 Command 객체입니다.
 */
public record CheckRateLimitCommand(
    String key,
    String ipAddress,
    int limitCount,
    long windowSizeInSeconds
) {
    
    /**
     * CheckRateLimitCommand 생성자입니다.
     * 
     * @param key Rate Limit을 체크할 키 (예: "token_refresh", "login_attempt")
     * @param ipAddress IP 주소
     * @param limitCount 제한 횟수
     * @param windowSizeInSeconds 시간 윈도우 크기 (초)
     * @throws IllegalArgumentException 파라미터가 유효하지 않은 경우
     */
    public CheckRateLimitCommand {
        Objects.requireNonNull(key, "Rate Limit 키가 필요합니다.");
        Objects.requireNonNull(ipAddress, "IP 주소가 필요합니다.");
        
        if (key.isBlank()) {
            throw new IllegalArgumentException("Rate Limit 키는 빈 값일 수 없습니다.");
        }
        if (ipAddress.isBlank()) {
            throw new IllegalArgumentException("IP 주소는 빈 값일 수 없습니다.");
        }
        if (limitCount <= 0) {
            throw new IllegalArgumentException("제한 횟수는 0보다 커야 합니다.");
        }
        if (windowSizeInSeconds <= 0) {
            throw new IllegalArgumentException("시간 윈도우 크기는 0보다 커야 합니다.");
        }
    }
}
