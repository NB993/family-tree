package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FamilyMemberTagMapping JPA 엔티티입니다.
 * 멤버와 태그 간의 다대다 매핑을 저장합니다.
 */
@Entity
@Table(name = "family_member_tag_mapping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyMemberTagMappingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * FamilyMemberTagMappingJpaEntity 전체 필드 생성자입니다.
     * <p>
     * {@link #from(FamilyMemberTagMapping)} 정적 팩토리 메서드에서만 호출됩니다.
     *
     * @param id        매핑 ID (새로 생성 시 null)
     * @param tagId     태그 ID
     * @param memberId  멤버 ID
     * @param createdAt 생성 일시
     */
    private FamilyMemberTagMappingJpaEntity(
        final Long id,
        final Long tagId,
        final Long memberId,
        final LocalDateTime createdAt
    ) {
        this.id = id;
        this.tagId = tagId;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }

    /**
     * 도메인 객체를 JPA 엔티티로 변환합니다.
     *
     * @param domain FamilyMemberTagMapping 도메인 객체
     * @return JPA 엔티티
     */
    public static FamilyMemberTagMappingJpaEntity from(final FamilyMemberTagMapping domain) {
        Objects.requireNonNull(domain, "domain must not be null");

        return new FamilyMemberTagMappingJpaEntity(
            domain.getId(),
            domain.getTagId(),
            domain.getMemberId(),
            domain.getCreatedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return FamilyMemberTagMapping 도메인 객체
     */
    public FamilyMemberTagMapping toFamilyMemberTagMapping() {
        return FamilyMemberTagMapping.withId(id, tagId, memberId, createdAt);
    }
}
