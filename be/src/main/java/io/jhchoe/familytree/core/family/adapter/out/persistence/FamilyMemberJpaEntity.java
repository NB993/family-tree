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
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "birthday")
    private LocalDateTime birthday;

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
     * @param userId      사용자 ID
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
        String name,
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
        this.name = name;
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
            familyMember.getName(),
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
        return FamilyMember.existingMember(
            id,
            familyId,
            userId,
            name,
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
    
    /**
     * 테스트 목적으로만 사용되는 상태 설정자 메서드.
     * 참고: 이 메서드는 테스트에서만 사용되어야 하며, 실제 비즈니스 로직에서는 사용하지 않습니다.
     * 올바른 상태 변경 방법은 도메인 모델의 메서드를 통해 이루어져야 합니다.
     *
     * @param status 설정할 상태
     */
    void setStatus(FamilyMemberStatus status) {
        this.status = status;
    }
}

