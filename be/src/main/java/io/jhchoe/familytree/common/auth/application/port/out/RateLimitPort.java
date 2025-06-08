package io.jhchoe.familytree.common.auth.application.port.out;

/**
 * Rate Limiting 관리를 위한 아웃바운드 포트입니다.
 */
public interface RateLimitPort {

    /**
     * 지정된 키에 대한 Rate Limit을 체크하고 카운트를 증가시킵니다.
     *
     * @param key Rate Limit을 체크할 키
     * @param limitCount 제한 횟수
     * @param windowSizeInSeconds 시간 윈도우 크기 (초)
     * @return true: 요청 허용, false: Rate Limit 초과
     */
    boolean checkAndIncrement(String key, int limitCount, long windowSizeInSeconds);

    /**
     * 지정된 키의 현재 요청 횟수를 조회합니다.
     *
     * @param key Rate Limit 키
     * @return 현재 요청 횟수
     */
    int getCurrentCount(String key);

    /**
     * 지정된 키의 Rate Limit 정보를 초기화합니다.
     *
     * @param key Rate Limit 키
     */
    void reset(String key);
}
