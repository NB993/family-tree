package io.jhchoe.familytree.config;

import io.jhchoe.familytree.common.auth.application.port.out.RateLimitPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 테스트용 Rate Limit 설정입니다.
 */
@TestConfiguration
public class TestRateLimitConfiguration {

    /**
     * 테스트용 간단한 RateLimitPort 구현체를 제공합니다.
     */
    @Bean
    @Primary
    public RateLimitPort testRateLimitPort() {
        return new RateLimitPort() {
            private final ConcurrentHashMap<String, Integer> store = new ConcurrentHashMap<>();

            @Override
            public boolean checkAndIncrement(String key, int limitCount, long windowSizeInSeconds) {
                int currentCount = store.getOrDefault(key, 0);
                if (currentCount >= limitCount) {
                    return false;
                }
                store.put(key, currentCount + 1);
                return true;
            }

            @Override
            public int getCurrentCount(String key) {
                return store.getOrDefault(key, 0);
            }

            @Override
            public void reset(String key) {
                store.remove(key);
            }
        };
    }
}
