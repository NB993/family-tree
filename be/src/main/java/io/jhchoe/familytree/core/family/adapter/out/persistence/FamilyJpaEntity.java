package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.Family;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Family 엔티티를 나타내는 클래스입니다.
 * 이 클래스는 데이터베이스 레코드를 표현하며, Family 도메인 클래스로 변환할 수 있습니다.
 */
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted = false")
@Entity(name = "family")
public class FamilyJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "deleted")
    private boolean deleted;

    private FamilyJpaEntity(
        final Long id,
        final String name,
        final String description,
        final String profileUrl,
        final Boolean isPublic,
        final Long createdBy,
        final LocalDateTime createdAt,
        final Long modifiedBy,
        final LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.isPublic = isPublic;
    }

    /**
     * Family 도메인 객체를 FamilyEntity로 변환합니다.
     *
     * @param family 변환할 Family 객체
     * @return 변환된 FamilyEntity 객체
     */
    public static FamilyJpaEntity from(Family family) {
        return new FamilyJpaEntity(
            family.getId(),
            family.getName(),
            family.getDescription(),
            family.getProfileUrl(),
            family.getIsPublic(),
            family.getCreatedBy(),
            family.getCreatedAt(),
            family.getModifiedBy(),
            family.getModifiedAt()
        );
    }

    /**
     * FamilyEntity를 Family 도메인 객체로 변환합니다.
     *
     * @return 변환된 Family 객체
     */
    public Family toFamily() {
        return Family.withId(id, name, description, profileUrl, isPublic, getCreatedBy(), getCreatedAt(), getModifiedBy(), getModifiedAt());
    }

    public void update(String name, String description, String profileUrl, Boolean isPublic) {
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.isPublic = isPublic;
    }
}
