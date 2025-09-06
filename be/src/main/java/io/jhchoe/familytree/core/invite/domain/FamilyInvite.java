package io.jhchoe.familytree.core.invite.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * FamilyInvite는 정보 수집을 위한 초대 링크를 나타내는 도메인 엔티티입니다.
 */
public final class FamilyInvite {
    private final Long id;
    private final Long requesterId;
    private final String inviteCode;
    private final LocalDateTime expiresAt;
    private final FamilyInviteStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private FamilyInvite(
        final Long id,
        final Long requesterId,
        final String inviteCode,
        final LocalDateTime expiresAt,
        final FamilyInviteStatus status,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(requesterId, "requesterId must not be null");
        Objects.requireNonNull(inviteCode, "inviteCode must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        Objects.requireNonNull(status, "status must not be null");

        this.id = id;
        this.requesterId = requesterId;
        this.inviteCode = inviteCode;
        this.expiresAt = expiresAt;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    /**
     * 새로운 초대를 생성합니다.
     *
     * @param requesterId 초대를 생성한 사용자 ID
     * @return 새로 생성된 초대 (ID는 null, inviteCode는 UUID)
     */
    public static FamilyInvite newInvite(
        final Long requesterId
    ) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expiresAt = now.plusDays(1);
        
        return new FamilyInvite(
            null,
            requesterId,
            UUID.randomUUID().toString(),
            expiresAt,
            FamilyInviteStatus.ACTIVE,
            now,
            now
        );
    }

    /**
     * 기존 초대 데이터를 도메인 객체로 복원합니다.
     *
     * @param id 초대 ID
     * @param requesterId 초대를 생성한 사용자 ID
     * @param inviteCode 초대 코드
     * @param expiresAt 만료 시간
     * @param status 초대 상태
     * @param createdAt 생성 시간
     * @param modifiedAt 수정 시간
     * @return 복원된 초대 (ID 포함)
     */
    public static FamilyInvite withId(
        final Long id,
        final Long requesterId,
        final String inviteCode,
        final LocalDateTime expiresAt,
        final FamilyInviteStatus status,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        
        return new FamilyInvite(
            id,
            requesterId,
            inviteCode,
            expiresAt,
            status,
            createdAt,
            modifiedAt
        );
    }

    /**
     * 초대가 만료되었는지 확인합니다.
     *
     * @return 만료 여부
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt) || FamilyInviteStatus.EXPIRED.equals(status);
    }

    /**
     * 초대가 활성 상태인지 확인합니다.
     *
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return FamilyInviteStatus.ACTIVE.equals(status) && !isExpired();
    }

    /**
     * 초대를 완료 상태로 변경합니다.
     *
     * @return 완료 상태로 변경된 새로운 초대 객체
     */
    public FamilyInvite complete() {
        return new FamilyInvite(
            id,
            requesterId,
            inviteCode,
            expiresAt,
            FamilyInviteStatus.COMPLETED,
            createdAt,
            LocalDateTime.now()
        );
    }

    /**
     * 초대를 만료 상태로 변경합니다.
     *
     * @return 만료 상태로 변경된 새로운 초대 객체
     */
    public FamilyInvite expire() {
        return new FamilyInvite(
            id,
            requesterId,
            inviteCode,
            expiresAt,
            FamilyInviteStatus.EXPIRED,
            createdAt,
            LocalDateTime.now()
        );
    }

    public Long getId() {
        return id;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public FamilyInviteStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
}