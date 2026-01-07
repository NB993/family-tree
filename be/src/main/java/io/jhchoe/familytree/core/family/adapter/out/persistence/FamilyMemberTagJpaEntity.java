package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FamilyMemberTagJpaEntity 클래스는 FamilyMember 태그를 DB에 저장하기 위한 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "family_member_tag")
public class FamilyMemberTagJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "color", nullable = false, length = 7)
    private String color;

    /**
     * FamilyMemberTagJpaEntity 객체를 생성하는 생성자입니다.
     *
     * @param id         ID
     * @param familyId   Family ID
     * @param name       태그 이름
     * @param color      태그 색상 (HEX)
     * @param createdBy  생성자 ID
     * @param createdAt  생성 일시
     * @param modifiedBy 수정자 ID
     * @param modifiedAt 수정 일시
     */
    private FamilyMemberTagJpaEntity(
        Long id,
        Long familyId,
        String name,
        String color,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.familyId = familyId;
        this.name = name;
        this.color = color;
    }

    /**
     * FamilyMemberTag 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param tag 도메인 객체
     * @return JPA 엔티티
     */
    public static FamilyMemberTagJpaEntity from(FamilyMemberTag tag) {
        Objects.requireNonNull(tag, "tag must not be null");

        return new FamilyMemberTagJpaEntity(
            tag.getId(),
            tag.getFamilyId(),
            tag.getName(),
            tag.getColor(),
            tag.getCreatedBy(),
            tag.getCreatedAt(),
            tag.getModifiedBy(),
            tag.getModifiedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 도메인 객체
     */
    public FamilyMemberTag toFamilyMemberTag() {
        return FamilyMemberTag.withId(
            id,
            familyId,
            name,
            color,
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}
