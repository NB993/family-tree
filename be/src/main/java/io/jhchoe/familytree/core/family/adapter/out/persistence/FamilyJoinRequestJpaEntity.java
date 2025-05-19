package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Family 가입 신청 정보를 나타내는 JPA 엔티티 클래스입니다.
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted = false")
@Entity(name = "family_join_request")
public class FamilyJoinRequestJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FamilyJoinRequestStatus status;

    @Column(name = "deleted")
    private boolean deleted;

    /**
     * FamilyJoinRequestJpaEntity 객체를 생성하는 private 생성자입니다.
     *
     * @param id 가입 신청 ID
     * @param familyId 가입 신청할 Family ID
     * @param requesterId 신청자 ID
     * @param status 가입 신청 상태
     * @param createdBy 생성자 ID
     * @param createdAt 생성 일시
     * @param modifiedBy 수정자 ID
     * @param modifiedAt 수정 일시
     */
    private FamilyJoinRequestJpaEntity(
        Long id,
        Long familyId,
        Long requesterId,
        FamilyJoinRequestStatus status,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.familyId = familyId;
        this.requesterId = requesterId;
        this.status = status;
        this.deleted = false;
    }

    /**
     * FamilyJoinRequest 도메인 객체를 FamilyJoinRequestJpaEntity로 변환합니다.
     *
     * @param familyJoinRequest 변환할 FamilyJoinRequest 객체
     * @return 변환된 FamilyJoinRequestJpaEntity 객체
     */
    public static FamilyJoinRequestJpaEntity from(FamilyJoinRequest familyJoinRequest) {
        return new FamilyJoinRequestJpaEntity(
            familyJoinRequest.getId(),
            familyJoinRequest.getFamilyId(),
            familyJoinRequest.getRequesterId(),
            familyJoinRequest.getStatus(),
            familyJoinRequest.getCreatedBy(),
            familyJoinRequest.getCreatedAt(),
            familyJoinRequest.getModifiedBy(),
            familyJoinRequest.getModifiedAt()
        );
    }

    /**
     * FamilyJoinRequestJpaEntity를 FamilyJoinRequest 도메인 객체로 변환합니다.
     *
     * @return 변환된 FamilyJoinRequest 객체
     */
    public FamilyJoinRequest toFamilyJoinRequest() {
        return FamilyJoinRequest.withId(
            id,
            familyId,
            requesterId,
            status,
            getCreatedAt(),
            getCreatedBy(),
            getModifiedAt(),
            getModifiedBy()
        );
    }
}
