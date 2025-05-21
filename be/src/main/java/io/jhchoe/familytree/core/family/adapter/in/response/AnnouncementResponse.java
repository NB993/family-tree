package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.Announcement;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 공지사항 조회 응답 DTO입니다.
 */
@Getter
public class AnnouncementResponse {
    
    private final Long id;
    private final String title;
    private final String content;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;
    
    /**
     * 응답 객체를 생성합니다.
     *
     * @param id         ID
     * @param title      제목
     * @param content    내용
     * @param createdBy  작성자 ID
     * @param createdAt  작성 일시
     * @param modifiedBy 수정자 ID
     * @param modifiedAt 수정 일시
     */
    private AnnouncementResponse(
        Long id,
        String title,
        String content,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }
    
    /**
     * 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param announcement 도메인 객체
     * @return 응답 DTO
     */
    public static AnnouncementResponse from(Announcement announcement) {
        return new AnnouncementResponse(
            announcement.getId(),
            announcement.getTitle(),
            announcement.getContent(),
            announcement.getCreatedBy(),
            announcement.getCreatedAt(),
            announcement.getModifiedBy(),
            announcement.getModifiedAt()
        );
    }
}
