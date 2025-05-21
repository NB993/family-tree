package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * FamilyMemberStatusHistory 클래스는 Family 구성원의 상태 변경 이력을 저장하는 도메인 클래스입니다.
 * <p>
 * 이 클래스는 이력의 고유 ID, 관련 Family ID, 구성원 ID, 상태, 사유 등의 주요 정보를 포함하며,
 * 생성자 및 팩토리 메서드를 통해 FamilyMemberStatusHistory 객체를 생성할 수 있습니다.
 * </p>
 */
@Getter
public class FamilyMemberStatusHistory {
    private final Long id;
    private final Long familyId;
    private final Long memberId;
    private final FamilyMemberStatus status;
    private final String reason;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    
    /**
     * FamilyMemberStatusHistory 객체 생성자.
     *
     * @param id        고유 ID
     * @param familyId  Family ID
     * @param memberId  구성원 ID
     * @param status    상태
     * @param reason    변경 사유
     * @param createdBy 생성한 사용자 ID
     * @param createdAt 생성 일시
     */
    private FamilyMemberStatusHistory(
        Long id,
        Long familyId,
        Long memberId,
        FamilyMemberStatus status,
        String reason,
        Long createdBy,
        LocalDateTime createdAt
    ) {
        this.id = id;
        this.familyId = familyId;
        this.memberId = memberId;
        this.status = status;
        this.reason = reason;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    
    /**
     * 새 상태 변경 이력 객체를 생성합니다.
     *
     * @param familyId Family ID
     * @param memberId 구성원 ID
     * @param status   상태
     * @param reason   변경 사유
     * @return 새로운 FamilyMemberStatusHistory 인스턴스 (ID 및 audit 필드 없음)
     */
    public static FamilyMemberStatusHistory create(
        Long familyId,
        Long memberId,
        FamilyMemberStatus status,
        String reason
    ) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(memberId, "memberId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        
        return new FamilyMemberStatusHistory(null, familyId, memberId, status, reason, null, null);
    }
    
    /**
     * ID와 Audit 정보를 포함한 기존 상태 변경 이력 객체를 생성합니다.
     *
     * @param id        고유 ID
     * @param familyId  Family ID
     * @param memberId  구성원 ID
     * @param status    상태
     * @param reason    변경 사유
     * @param createdBy 생성한 사용자 ID
     * @param createdAt 생성 일시
     * @return 새로운 FamilyMemberStatusHistory 인스턴스 (ID 및 audit 필드 포함)
     */
    public static FamilyMemberStatusHistory withId(
        Long id,
        Long familyId,
        Long memberId,
        FamilyMemberStatus status,
        String reason,
        Long createdBy,
        LocalDateTime createdAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(memberId, "memberId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        
        return new FamilyMemberStatusHistory(id, familyId, memberId, status, reason, createdBy, createdAt);
    }
}