package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

/**
 * 가족 관계 정보를 저장하는 JPA 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "family_member_relationship")
@Table(uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_family_member_relationship",
        columnNames = {"family_id", "from_member_id", "to_member_id"}
    )
})
public class FamilyMemberRelationshipJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "from_member_id", nullable = false)
    private Long fromMemberId;

    @Column(name = "to_member_id", nullable = false)
    private Long toMemberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private FamilyMemberRelationshipType relationshipType;

    @Column(name = "custom_relationship")
    private String customRelationship;

    @Column(name = "description")
    private String description;

    @Column(name = "deleted")
    private boolean deleted = false;

    /**
     * 가족 관계 JPA 엔티티 생성자입니다.
     *
     * @param id                 고유 식별자
     * @param familyId           가족 ID
     * @param fromMemberId       관계를 정의한 구성원 ID
     * @param toMemberId         관계가 정의된 대상 구성원 ID
     * @param relationshipType   관계 유형
     * @param customRelationship 사용자 정의 관계명
     * @param description        부가 설명
     * @param createdBy          생성자 ID
     * @param createdAt          생성 시간
     * @param modifiedBy         수정자 ID
     * @param modifiedAt         수정 시간
     */
    private FamilyMemberRelationshipJpaEntity(
        Long id,
        Long familyId,
        Long fromMemberId,
        Long toMemberId,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String description,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.familyId = familyId;
        this.fromMemberId = fromMemberId;
        this.toMemberId = toMemberId;
        this.relationshipType = relationshipType;
        this.customRelationship = customRelationship;
        this.description = description;
    }

    /**
     * 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param relationship 가족 관계 도메인 객체
     * @return 변환된 JPA 엔티티
     */
    public static FamilyMemberRelationshipJpaEntity from(FamilyMemberRelationship relationship) {
        Objects.requireNonNull(relationship, "relationship must not be null");

        return new FamilyMemberRelationshipJpaEntity(
            relationship.getId(),
            relationship.getFamilyId(),
            relationship.getFromMemberId(),
            relationship.getToMemberId(),
            relationship.getRelationshipType(),
            relationship.getCustomRelationship(),
            relationship.getDescription(),
            relationship.getCreatedBy(),
            relationship.getCreatedAt(),
            relationship.getModifiedBy(),
            relationship.getModifiedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 변환된 도메인 객체
     */
    public FamilyMemberRelationship toFamilyRelationship() {
        return FamilyMemberRelationship.withId(
            id,
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description,
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}
