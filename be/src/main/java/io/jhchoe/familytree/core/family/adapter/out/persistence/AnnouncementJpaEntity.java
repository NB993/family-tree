package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.family.domain.Announcement;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AnnouncementJpaEntity 클래스는 Family 내 공지사항을 DB에 저장하기 위한 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "family_announcement")
public class AnnouncementJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_id", nullable = false)
    private Long familyId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * AnnouncementJpaEntity 객체를 생성하는 생성자입니다.
     *
     * @param id          ID
     * @param familyId    Family ID
     * @param title       제목
     * @param content     내용
     * @param createdBy   작성자 ID
     * @param createdAt   작성 일시
     * @param modifiedBy  수정자 ID
     * @param modifiedAt  수정 일시
     */
    public AnnouncementJpaEntity(
        Long id,
        Long familyId,
        String title,
        String content,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.familyId = familyId;
        this.title = title;
        this.content = content;
    }

    /**
     * Announcement 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param announcement 도메인 객체
     * @return JPA 엔티티
     */
    public static AnnouncementJpaEntity from(Announcement announcement) {
        Objects.requireNonNull(announcement, "announcement must not be null");

        return new AnnouncementJpaEntity(
            announcement.getId(),
            announcement.getFamilyId(),
            announcement.getTitle(),
            announcement.getContent(),
            announcement.getCreatedBy(),
            announcement.getCreatedAt(),
            announcement.getModifiedBy(),
            announcement.getModifiedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 도메인 객체
     */
    public Announcement toDomainEntity() {
        return Announcement.withId(
            id,
            familyId,
            title,
            content,
            getCreatedBy(),
            getCreatedAt(),
            getModifiedBy(),
            getModifiedAt()
        );
    }
}
