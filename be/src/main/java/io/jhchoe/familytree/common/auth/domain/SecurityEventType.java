package io.jhchoe.familytree.common.auth.domain;

/**
 * 보안 이벤트 타입을 정의하는 열거형입니다.
 */
public enum SecurityEventType {
    
    /**
     * 인증 실패 이벤트
     */
    AUTHENTICATION_FAILURE,
    
    /**
     * 토큰 만료 이벤트
     */
    TOKEN_EXPIRED,
    
    /**
     * 유효하지 않은 토큰 사용 시도
     */
    INVALID_TOKEN_ATTEMPT,
    
    /**
     * 블랙리스트 토큰 사용 시도
     */
    BLACKLISTED_TOKEN_ATTEMPT,
    
    /**
     * Rate Limit 초과 이벤트
     */
    RATE_LIMIT_EXCEEDED,
    
    /**
     * 의심스러운 액세스 패턴
     */
    SUSPICIOUS_ACCESS_PATTERN,
    
    /**
     * 성공적인 로그아웃
     */
    LOGOUT_SUCCESS
}
