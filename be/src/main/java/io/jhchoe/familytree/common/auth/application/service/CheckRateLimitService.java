package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.CheckRateLimitCommand;
import io.jhchoe.familytree.common.auth.application.port.in.CheckRateLimitUseCase;
import io.jhchoe.familytree.common.auth.application.port.out.RateLimitPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Rate Limiting 체크 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckRateLimitService implements CheckRateLimitUseCase {

    private final RateLimitPort rateLimitPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkRateLimit(final CheckRateLimitCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Rate Limit 키 생성 (키 + IP 주소 조합)
        final String rateLimitKey = generateRateLimitKey(command.key(), command.ipAddress());

        // Rate Limit 체크
        final boolean isAllowed = rateLimitPort.checkAndIncrement(
            rateLimitKey,
            command.limitCount(),
            command.windowSizeInSeconds()
        );

        // Rate Limit 초과 시 로깅
        if (!isAllowed) {
            final int currentCount = rateLimitPort.getCurrentCount(rateLimitKey);
            log.warn("Rate limit exceeded for key: {} - IP: {} - Current count: {} - Limit: {}", 
                command.key(), 
                command.ipAddress(), 
                currentCount, 
                command.limitCount()
            );
        }

        return isAllowed;
    }

    /**
     * Rate Limit 키를 생성합니다.
     *
     * @param key 기본 키
     * @param ipAddress IP 주소
     * @return 생성된 Rate Limit 키
     */
    private String generateRateLimitKey(final String key, final String ipAddress) {
        return String.format("%s:%s", key, ipAddress);
    }
}
