package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
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
    private Long userId; // nullable - 비회원도 가능

    @Column(name = "kakao_id")
    private String kakaoId; // 카카오 ID

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Column(name = "relationship")
    private String relationship;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FamilyMemberStatus status;
    
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private FamilyMemberRole role;

    /**
     * FamilyMemberEntity 전체 필드를 초기화하는 생성자.
     *
     * @param id          고유 ID
     * @param familyId    Family ID
     * @param userId      사용자 ID (nullable - 비회원인 경우 null)
     * @param kakaoId     카카오 ID (nullable)
     * @param name        구성원 이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @param status      멤버 상태
     * @param role        멤버 역할
     * @param createdBy   생성한 사용자 ID
     * @param createdAt   생성 일시
     * @param modifiedBy  수정한 사용자 ID
     * @param modifiedAt  수정 일시
     */
    public FamilyMemberJpaEntity(
        Long id,
        Long familyId,
        Long userId,
        String kakaoId,
        String name,
        String relationship,
        String profileUrl,
        LocalDateTime birthday,
        String nationality,
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
        this.kakaoId = kakaoId;
        this.name = name;
        this.relationship = relationship;
        this.profileUrl = profileUrl;
        this.birthday = birthday;
        this.nationality = nationality;
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
            familyMember.getKakaoId(),
            familyMember.getName(),
            familyMember.getRelationship(),
            familyMember.getProfileUrl(),
            familyMember.getBirthday(),
            familyMember.getNationality(),
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
            kakaoId,
            name,
            relationship,
            profileUrl,
            birthday,
            nationality,
            status,
            role != null ? role : FamilyMemberRole.MEMBER, // 기본 역할은 MEMBER
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}

