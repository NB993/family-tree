package io.jhchoe.familytree.common.auth.adapter.out;

import io.jhchoe.familytree.common.auth.application.port.out.RateLimitPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 메모리 기반 Rate Limit 구현체입니다.
 * 개발/테스트 환경에서 사용하며, 프로덕션에서는 Redis 기반 구현체로 교체해야 합니다.
 */
@Slf4j
@Component
public class InMemoryRateLimitAdapter implements RateLimitPort {

    private final Clock clock;
    private final ConcurrentHashMap<String, RateLimitWindow> rateLimitStore;

    public InMemoryRateLimitAdapter() {
        this.clock = Clock.systemUTC();
        this.rateLimitStore = new ConcurrentHashMap<>();
    }

    // 테스트용 생성자
    public InMemoryRateLimitAdapter(Clock clock) {
        this.clock = clock;
        this.rateLimitStore = new ConcurrentHashMap<>();
    }

    @Override
    public boolean checkAndIncrement(String key, int limitCount, long windowSizeInSeconds) {
        if (key == null || key.trim().isEmpty()) {
            log.warn("Rate limit key is null or empty");
            return false;
        }

        final Instant now = clock.instant();
        final RateLimitWindow window = rateLimitStore.computeIfAbsent(key, k -> new RateLimitWindow(now, windowSizeInSeconds));

        synchronized (window) {
            // 윈도우가 만료되었으면 초기화
            if (window.isExpired(now)) {
                window.reset(now, windowSizeInSeconds);
            }

            // 현재 카운트가 제한을 넘었는지 확인
            if (window.getCurrentCount() >= limitCount) {
                log.debug("Rate limit exceeded for key: {}, current: {}, limit: {}", 
                    key, window.getCurrentCount(), limitCount);
                return false;
            }

            // 카운트 증가
            window.increment();
            log.debug("Rate limit check passed for key: {}, current: {}, limit: {}", 
                key, window.getCurrentCount(), limitCount);
            return true;
        }
    }

    @Override
    public int getCurrentCount(String key) {
        if (key == null || key.trim().isEmpty()) {
            return 0;
        }

        final RateLimitWindow window = rateLimitStore.get(key);
        if (window == null) {
            return 0;
        }

        synchronized (window) {
            final Instant now = clock.instant();
            if (window.isExpired(now)) {
                return 0;
            }
            return window.getCurrentCount();
        }
    }

    @Override
    public void reset(String key) {
        if (key == null || key.trim().isEmpty()) {
            return;
        }

        rateLimitStore.remove(key);
        log.debug("Rate limit reset for key: {}", key);
    }

    /**
     * 모든 Rate Limit 데이터를 초기화합니다. (테스트용)
     */
    public void resetAll() {
        rateLimitStore.clear();
        log.debug("All rate limit data cleared");
    }

    /**
     * Rate Limit 윈도우를 나타내는 내부 클래스입니다.
     */
    private static class RateLimitWindow {
        private Instant windowStart;
        private long windowSizeInSeconds;
        private final AtomicInteger count;

        public RateLimitWindow(Instant windowStart, long windowSizeInSeconds) {
            this.windowStart = windowStart;
            this.windowSizeInSeconds = windowSizeInSeconds;
            this.count = new AtomicInteger(0);
        }

        public boolean isExpired(Instant now) {
            return now.isAfter(windowStart.plusSeconds(windowSizeInSeconds));
        }

        public void reset(Instant newWindowStart, long newWindowSizeInSeconds) {
            this.windowStart = newWindowStart;
            this.windowSizeInSeconds = newWindowSizeInSeconds;
            this.count.set(0);
        }

        public int getCurrentCount() {
            return count.get();
        }

        public void increment() {
            count.incrementAndGet();
        }
    }
}
