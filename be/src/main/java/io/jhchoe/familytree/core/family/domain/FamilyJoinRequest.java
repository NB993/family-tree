package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * Family 가입 신청 정보를 나타내는 도메인 클래스입니다.
 */
@Getter
public class FamilyJoinRequest {
    private final Long id;
    private final Long familyId;
    private final Long requesterId;
    private final FamilyJoinRequestStatus status;
    private final LocalDateTime createdAt;
    private final Long createdBy;
    private final LocalDateTime modifiedAt;
    private final Long modifiedBy;

    /**
     * Family 가입 신청 객체를 생성하는 private 생성자입니다.
     *
     * @param id 가입 신청 ID
     * @param familyId 가입 신청할 Family ID
     * @param requesterId 신청자 ID (FTUser ID)
     * @param status 가입 신청 상태
     * @param createdAt 생성 일시
     * @param createdBy 생성자 ID
     * @param modifiedAt 수정 일시
     * @param modifiedBy 수정자 ID
     */
    private FamilyJoinRequest(
        Long id,
        Long familyId,
        Long requesterId,
        FamilyJoinRequestStatus status,
        LocalDateTime createdAt,
        Long createdBy,
        LocalDateTime modifiedAt,
        Long modifiedBy
    ) {
        this.id = id;
        this.familyId = familyId;
        this.requesterId = requesterId;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
    }

    /**
     * 새로운 가입 신청을 생성하는 정적 팩토리 메서드입니다.
     *
     * @param familyId 가입 신청할 Family ID
     * @param requesterId 신청자 ID (FTUser ID)
     * @return 생성된 FamilyJoinRequest 객체
     */
    public static FamilyJoinRequest newRequest(Long familyId, Long requesterId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(requesterId, "requesterId must not be null");
        
        return new FamilyJoinRequest(
            null,
            familyId,
            requesterId,
            FamilyJoinRequestStatus.PENDING,
            null,
            null,
            null,
            null
        );
    }

    /**
     * 기존 가입 신청 정보로 FamilyJoinRequest 객체를 생성하는 정적 팩토리 메서드입니다.
     *
     * @param id 가입 신청 ID
     * @param familyId 가입 신청할 Family ID
     * @param requesterId 신청자 ID (FTUser ID)
     * @param status 가입 신청 상태
     * @param createdAt 생성 일시
     * @param createdBy 생성자 ID
     * @param modifiedAt 수정 일시
     * @param modifiedBy 수정자 ID
     * @return 생성된 FamilyJoinRequest 객체
     */
    public static FamilyJoinRequest withId(
        Long id,
        Long familyId,
        Long requesterId,
        FamilyJoinRequestStatus status,
        LocalDateTime createdAt,
        Long createdBy,
        LocalDateTime modifiedAt,
        Long modifiedBy
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(requesterId, "requesterId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        
        return new FamilyJoinRequest(
            id, familyId, requesterId, status, createdAt, createdBy, modifiedAt, modifiedBy
        );
    }

    /**
     * 가입 신청을 승인 상태로 변경한 새 객체를 반환합니다.
     *
     * @return 승인 상태로 변경된 새 FamilyJoinRequest 객체
     */
    public FamilyJoinRequest approve() {
        return new FamilyJoinRequest(
            this.id, this.familyId, this.requesterId, FamilyJoinRequestStatus.APPROVED,
            this.createdAt, this.createdBy, this.modifiedAt, this.modifiedBy
        );
    }

    /**
     * 가입 신청을 거절 상태로 변경한 새 객체를 반환합니다.
     *
     * @return 거절 상태로 변경된 새 FamilyJoinRequest 객체
     */
    public FamilyJoinRequest reject() {
        return new FamilyJoinRequest(
            this.id, this.familyId, this.requesterId, FamilyJoinRequestStatus.REJECTED,
            this.createdAt, this.createdBy, this.modifiedAt, this.modifiedBy
        );
    }

    /**
     * 현재 가입 신청이 대기 상태인지 확인합니다.
     *
     * @return 대기 상태이면 true, 아니면 false
     */
    public boolean isPending() {
        return FamilyJoinRequestStatus.PENDING.equals(this.status);
    }

    /**
     * 현재 가입 신청이 승인 상태인지 확인합니다.
     *
     * @return 승인 상태이면 true, 아니면 false
     */
    public boolean isApproved() {
        return FamilyJoinRequestStatus.APPROVED.equals(this.status);
    }

    /**
     * 현재 가입 신청이 거절 상태인지 확인합니다.
     *
     * @return 거절 상태이면 true, 아니면 false
     */
    public boolean isRejected() {
        return FamilyJoinRequestStatus.REJECTED.equals(this.status);
    }
}
