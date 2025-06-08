package io.jhchoe.familytree.common.auth.application.port.in;

import io.jhchoe.familytree.common.auth.domain.SecurityEventType;
import java.util.Objects;

/**
 * 보안 이벤트 로깅 요청을 나타내는 Command 객체입니다.
 */
public record LogSecurityEventCommand(
    SecurityEventType eventType,
    String userId,
    String ipAddress,
    String userAgent,
    String description
) {
    
    /**
     * LogSecurityEventCommand 생성자입니다.
     * 
     * @param eventType 이벤트 타입 (필수)
     * @param userId 사용자 ID (선택적)
     * @param ipAddress IP 주소 (필수)
     * @param userAgent User-Agent (선택적)
     * @param description 이벤트 설명 (필수)
     * @throws IllegalArgumentException 필수 파라미터가 null이거나 빈 문자열인 경우
     */
    public LogSecurityEventCommand {
        Objects.requireNonNull(eventType, "이벤트 타입이 필요합니다.");
        Objects.requireNonNull(ipAddress, "IP 주소가 필요합니다.");
        Objects.requireNonNull(description, "이벤트 설명이 필요합니다.");
        
        if (ipAddress.isBlank()) {
            throw new IllegalArgumentException("IP 주소는 빈 값일 수 없습니다.");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("이벤트 설명은 빈 값일 수 없습니다.");
        }
    }
}
