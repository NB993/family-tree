package io.jhchoe.familytree.common.auth.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 보안 관련 이벤트를 나타내는 도메인 객체입니다.
 */
public final class SecurityEvent {
    
    private final Long id;
    private final SecurityEventType eventType;
    private final String userId;
    private final String ipAddress;
    private final String userAgent;
    private final String description;
    private final LocalDateTime occurredAt;

    private SecurityEvent(
        final Long id,
        final SecurityEventType eventType,
        final String userId,
        final String ipAddress,
        final String userAgent,
        final String description,
        final LocalDateTime occurredAt
    ) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(ipAddress, "ipAddress must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        
        if (description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        if (ipAddress.isBlank()) {
            throw new IllegalArgumentException("ipAddress must not be blank");
        }
        
        this.id = id;
        this.eventType = eventType;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.description = description;
        this.occurredAt = occurredAt;
    }

    /**
     * 새로운 보안 이벤트를 생성합니다.
     *
     * @param eventType 이벤트 타입
     * @param userId 사용자 ID (선택적)
     * @param ipAddress IP 주소
     * @param userAgent User-Agent (선택적)
     * @param description 이벤트 설명
     * @return 새로 생성된 보안 이벤트
     */
    public static SecurityEvent newEvent(
        final SecurityEventType eventType,
        final String userId,
        final String ipAddress,
        final String userAgent,
        final String description
    ) {
        return new SecurityEvent(
            null,
            eventType,
            userId,
            ipAddress,
            userAgent,
            description,
            LocalDateTime.now()
        );
    }

    /**
     * 기존 보안 이벤트 데이터를 도메인 객체로 복원합니다.
     *
     * @param id 이벤트 ID
     * @param eventType 이벤트 타입
     * @param userId 사용자 ID
     * @param ipAddress IP 주소
     * @param userAgent User-Agent
     * @param description 이벤트 설명
     * @param occurredAt 발생 시간
     * @return 복원된 보안 이벤트
     */
    public static SecurityEvent withId(
        final Long id,
        final SecurityEventType eventType,
        final String userId,
        final String ipAddress,
        final String userAgent,
        final String description,
        final LocalDateTime occurredAt
    ) {
        return new SecurityEvent(
            id,
            eventType,
            userId,
            ipAddress,
            userAgent,
            description,
            occurredAt
        );
    }

    /**
     * 이벤트 ID를 반환합니다.
     *
     * @return 이벤트 ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 이벤트 타입을 반환합니다.
     *
     * @return 이벤트 타입
     */
    public SecurityEventType getEventType() {
        return eventType;
    }

    /**
     * 사용자 ID를 반환합니다.
     *
     * @return 사용자 ID (null 가능)
     */
    public String getUserId() {
        return userId;
    }

    /**
     * IP 주소를 반환합니다.
     *
     * @return IP 주소
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * User-Agent를 반환합니다.
     *
     * @return User-Agent (null 가능)
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 이벤트 설명을 반환합니다.
     *
     * @return 이벤트 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 이벤트 발생 시간을 반환합니다.
     *
     * @return 이벤트 발생 시간
     */
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        final SecurityEvent that = (SecurityEvent) o;
        return Objects.equals(id, that.id) && 
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(ipAddress, that.ipAddress) &&
               Objects.equals(userAgent, that.userAgent) &&
               Objects.equals(description, that.description) &&
               Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventType, userId, ipAddress, userAgent, description, occurredAt);
    }

    @Override
    public String toString() {
        return "SecurityEvent{" +
               "id=" + id +
               ", eventType=" + eventType +
               ", userId='" + userId + '\'' +
               ", ipAddress='" + ipAddress + '\'' +
               ", userAgent='" + userAgent + '\'' +
               ", description='" + description + '\'' +
               ", occurredAt=" + occurredAt +
               '}';
    }
}
