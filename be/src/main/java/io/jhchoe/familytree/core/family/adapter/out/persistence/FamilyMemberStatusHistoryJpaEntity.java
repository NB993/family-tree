package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.CreatorBaseEntity;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatusHistory;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FamilyMemberStatusHistoryJpaEntity 클래스는 구성원 상태 변경 이력을 DB에 저장하기 위한 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "family_member_status_history")
public class FamilyMemberStatusHistoryJpaEntity extends CreatorBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FamilyMemberStatus status;

    @Column(name = "reason")
    private String reason;

    /**
     * FamilyMemberStatusHistoryJpaEntity 객체를 생성하는 생성자입니다.
     *
     * @param id        ID
     * @param familyId  Family ID
     * @param memberId  Member ID
     * @param status    변경된 상태
     * @param reason    변경 사유
     * @param createdBy 변경을 수행한 사용자 ID
     * @param createdAt 변경 일시
     */
    public FamilyMemberStatusHistoryJpaEntity(
        Long id,
        Long familyId,
        Long memberId,
        FamilyMemberStatus status,
        String reason,
        Long createdBy,
        LocalDateTime createdAt
    ) {
        super(createdBy, createdAt);
        this.id = id;
        this.familyId = familyId;
        this.memberId = memberId;
        this.status = status;
        this.reason = reason;
    }

    /**
     * FamilyMemberStatusHistory 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param history 도메인 객체
     * @return JPA 엔티티
     */
    public static FamilyMemberStatusHistoryJpaEntity from(FamilyMemberStatusHistory history) {
        Objects.requireNonNull(history, "statusHistory must not be null");

        return new FamilyMemberStatusHistoryJpaEntity(
            history.getId(),
            history.getFamilyId(),
            history.getMemberId(),
            history.getStatus(),
            history.getReason(),
            history.getCreatedBy(),
            history.getCreatedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 도메인 객체
     */
    public FamilyMemberStatusHistory toDomainEntity() {
        return FamilyMemberStatusHistory.withId(
            id,
            familyId,
            memberId,
            status,
            reason,
            getCreatedBy(),
            getCreatedAt()
        );
    }
}
