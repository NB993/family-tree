package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * Announcement 클래스는 Family 내 공지사항의 정보를 저장하는 도메인 클래스입니다.
 * <p>
 * 이 클래스는 공지사항의 고유 ID, 제목, 내용 등의 주요 정보를 포함하며,
 * 생성자 및 팩토리 메서드를 통해 Announcement 객체를 생성할 수 있습니다.
 * </p>
 */
@Getter
public class Announcement {
    private final Long id;
    private final Long familyId;
    private final String title;
    private final String content;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;
    
    /**
     * Announcement 객체 생성자.
     *
     * @param id          고유 ID
     * @param familyId    Family ID
     * @param title       제목
     * @param content     내용
     * @param createdBy   생성한 사용자 ID
     * @param createdAt   생성 일시
     * @param modifiedBy  수정한 사용자 ID
     * @param modifiedAt  수정 일시
     */
    private Announcement(
        Long id,
        Long familyId,
        String title,
        String content,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.familyId = familyId;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }
    
    /**
     * 새 공지사항 객체를 생성합니다.
     *
     * @param familyId    Family ID
     * @param title       제목
     * @param content     내용
     * @return 새로운 Announcement 인스턴스 (ID 및 audit 필드 없음)
     */
    public static Announcement create(Long familyId, String title, String content) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        
        return new Announcement(null, familyId, title, content, null, null, null, null);
    }
    
    /**
     * ID를 포함한 기존 공지사항 객체를 생성합니다.
     *
     * @param id          고유 ID
     * @param familyId    Family ID
     * @param title       제목
     * @param content     내용
     * @param createdBy   생성한 사용자 ID
     * @param createdAt   생성 일시
     * @param modifiedBy  수정한 사용자 ID
     * @param modifiedAt  수정 일시
     * @return 새로운 Announcement 인스턴스 (ID 및 audit 필드 포함)
     */
    public static Announcement withId(
        Long id,
        Long familyId,
        String title,
        String content,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        
        return new Announcement(id, familyId, title, content, createdBy, createdAt, modifiedBy, modifiedAt);
    }
    
    /**
     * 공지사항을 수정하여 새 객체를 반환합니다.
     *
     * @param title    제목
     * @param content  내용
     * @return 수정된 내용의 새로운 Announcement 인스턴스
     */
    public Announcement update(String title, String content) {
        Objects.requireNonNull(title, "title must not be null");
        
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        
        return new Announcement(
            this.id, this.familyId, title, content,
            this.createdBy, this.createdAt, this.modifiedBy, this.modifiedAt
        );
    }
}