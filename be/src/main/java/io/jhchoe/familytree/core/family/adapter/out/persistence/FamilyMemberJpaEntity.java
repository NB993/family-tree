package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.MemberStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * FamilyMemberEntity 클래스는 FamilyMember 도메인 객체를 데이터베이스에 저장하기 위한 엔티티입니다.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted = false")
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
    private MemberStatus status;

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
     * @param createdBy   생성한 사용자 ID
     * @param createdAt   생성 일시
     * @param modifiedBy  수정한 사용자 ID
     * @param modifiedAt  수정 일시
     */
    private FamilyMemberJpaEntity(
        Long id,
        Long familyId,
        Long userId,
        String name,
        String profileUrl,
        LocalDateTime birthday,
        String nationality,
        MemberStatus status,
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
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}
