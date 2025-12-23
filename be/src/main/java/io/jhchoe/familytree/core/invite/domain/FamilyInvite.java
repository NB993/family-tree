package io.jhchoe.familytree.core.invite.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * FamilyInvite는 정보 수집을 위한 초대 링크를 나타내는 도메인 엔티티입니다.
 */
public final class FamilyInvite {
    private final Long id;
    private final Long familyId;
    private final Long requesterId;
    private final String inviteCode;
    private final LocalDateTime expiresAt;
    private final Integer maxUses;
    private final Integer usedCount;
    private final FamilyInviteStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private FamilyInvite(
        final Long id,
        final Long familyId,
        final Long requesterId,
        final String inviteCode,
        final LocalDateTime expiresAt,
        final Integer maxUses,
        final Integer usedCount,
        final FamilyInviteStatus status,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(requesterId, "requesterId must not be null");
        Objects.requireNonNull(inviteCode, "inviteCode must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        Objects.requireNonNull(status, "status must not be null");

        this.id = id;
        this.familyId = familyId;
        this.requesterId = requesterId;
        this.inviteCode = inviteCode;
        this.expiresAt = expiresAt;
        this.maxUses = maxUses;
        this.usedCount = usedCount != null ? usedCount : 0;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    /**
     * 새로운 초대를 생성합니다.
     *
     * @param familyId 초대할 가족 ID
     * @param requesterId 초대를 생성한 사용자 ID
     * @param maxUses 최대 사용 횟수 (null이면 무제한)
     * @return 새로 생성된 초대 (ID는 null, inviteCode는 UUID)
     */
    public static FamilyInvite newInvite(
        final Long familyId,
        final Long requesterId,
        final Integer maxUses
    ) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expiresAt = now.plusDays(1);

        return new FamilyInvite(
            null,
            familyId,
            requesterId,
            UUID.randomUUID().toString(),
            expiresAt,
            maxUses,
            0,
            FamilyInviteStatus.ACTIVE,
            now,
            now
        );
    }

    /**
     * 기존 초대 데이터를 도메인 객체로 복원합니다.
     *
     * @param id 초대 ID
     * @param familyId 초대할 가족 ID
     * @param requesterId 초대를 생성한 사용자 ID
     * @param inviteCode 초대 코드
     * @param expiresAt 만료 시간
     * @param maxUses 최대 사용 횟수
     * @param usedCount 사용된 횟수
     * @param status 초대 상태
     * @param createdAt 생성 시간
     * @param modifiedAt 수정 시간
     * @return 복원된 초대 (ID 포함)
     */
    public static FamilyInvite withId(
        final Long id,
        final Long familyId,
        final Long requesterId,
        final String inviteCode,
        final LocalDateTime expiresAt,
        final Integer maxUses,
        final Integer usedCount,
        final FamilyInviteStatus status,
        final LocalDateTime createdAt,
        final LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");

        return new FamilyInvite(
            id,
            familyId,
            requesterId,
            inviteCode,
            expiresAt,
            maxUses,
            usedCount,
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
            familyId,
            requesterId,
            inviteCode,
            expiresAt,
            maxUses,
            usedCount,
            FamilyInviteStatus.COMPLETED,
            createdAt,
            modifiedAt // JPA Auditing이 자동으로 업데이트
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
            familyId,
            requesterId,
            inviteCode,
            expiresAt,
            maxUses,
            usedCount,
            FamilyInviteStatus.EXPIRED,
            createdAt,
            modifiedAt // JPA Auditing이 자동으로 업데이트
        );
    }
    
    /**
     * 초대 사용 횟수를 증가시킵니다.
     *
     * @return 사용 횟수가 증가된 새로운 초대 객체
     */
    public FamilyInvite incrementUsedCount() {
        return new FamilyInvite(
            id,
            familyId,
            requesterId,
            inviteCode,
            expiresAt,
            maxUses,
            usedCount + 1,
            status,
            createdAt,
            modifiedAt // JPA Auditing이 자동으로 업데이트
        );
    }

    public Long getId() {
        return id;
    }

    public Long getFamilyId() {
        return familyId;
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
    
    public Integer getMaxUses() {
        return maxUses;
    }
    
    public Integer getUsedCount() {
        return usedCount;
    }
}