package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

/**
 * FamilyMemberEntity 클래스는 FamilyMember 도메인 객체를 데이터베이스에 저장하기 위한 엔티티입니다.
 * <p>
 * name, profileUrl, birthday는 Family별로 독립적으로 관리됩니다.
 * 초대 수락 시 User 정보를 복사하며, 이후 Family 주인이 수정할 수 있습니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "family_member")
public class FamilyMemberJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "user_id")
    private Long userId; // nullable - 수동 등록(애완동물, 아이 등)인 경우 null

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "birthday_type", length = 10, nullable = true)
    private BirthdayType birthdayType;

    @Column(name = "relationship_type", nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private FamilyMemberRelationshipType relationshipType;

    @Column(name = "custom_relationship", nullable = true, length = 50)
    private String customRelationship;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FamilyMemberStatus status;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private FamilyMemberRole role;

    /**
     * FamilyMemberEntity 전체 필드를 초기화하는 생성자.
     *
     * @param id                 고유 ID
     * @param familyId           Family ID
     * @param userId             사용자 ID (nullable - 수동 등록인 경우 null)
     * @param name               구성원 이름
     * @param relationshipType   관계 타입 (nullable)
     * @param customRelationship CUSTOM일 때 사용자 입력값 (nullable)
     * @param profileUrl         프로필 URL
     * @param birthday           생일
     * @param birthdayType       생일 유형 (양력/음력)
     * @param status             멤버 상태
     * @param role               멤버 역할
     * @param createdBy          생성한 사용자 ID
     * @param createdAt          생성 일시
     * @param modifiedBy         수정한 사용자 ID
     * @param modifiedAt         수정 일시
     */
    private FamilyMemberJpaEntity(
        Long id,
        Long familyId,
        Long userId,
        String name,
        FamilyMemberRelationshipType relationshipType,
        String customRelationship,
        String profileUrl,
        LocalDateTime birthday,
        BirthdayType birthdayType,
        FamilyMemberStatus status,
        FamilyMemberRole role,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.familyId = familyId;
        this.userId = userId;
        this.name = name;
        this.relationshipType = relationshipType;
        this.customRelationship = customRelationship;
        this.profileUrl = profileUrl;
        this.birthday = birthday;
        this.birthdayType = birthdayType;
        this.status = status;
        this.role = role;
    }

    /**
     * FamilyMember 객체를 FamilyMemberEntity로 변환합니다.
     *
     * @param familyMember 변환할 FamilyMember 객체
     * @return 변환된 FamilyMemberEntity 객체
     */
    public static FamilyMemberJpaEntity from(FamilyMember familyMember) {
        Objects.requireNonNull(familyMember, "familyMember must not be null");

        return new FamilyMemberJpaEntity(
            familyMember.getId(),
            familyMember.getFamilyId(),
            familyMember.getUserId(),
            familyMember.getName(),
            familyMember.getRelationshipType(),
            familyMember.getCustomRelationship(),
            familyMember.getProfileUrl(),
            familyMember.getBirthday(),
            familyMember.getBirthdayType(),
            familyMember.getStatus(),
            familyMember.getRole(),
            familyMember.getCreatedBy(),
            familyMember.getCreatedAt(),
            familyMember.getModifiedBy(),
            familyMember.getModifiedAt()
        );
    }

    /**
     * FamilyMemberEntity를 FamilyMember 객체로 변환합니다.
     *
     * @return 변환된 FamilyMember 객체
     */
    public FamilyMember toFamilyMember() {
        return FamilyMember.withId(
            id,
            familyId,
            userId,
            name,
            relationshipType,
            customRelationship,
            profileUrl,
            birthday,
            birthdayType,
            status,
            role != null ? role : FamilyMemberRole.MEMBER, // 기본 역할은 MEMBER
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}

