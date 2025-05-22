package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.Announcement;
import java.time.LocalDateTime;

/**
 * 공지사항 조회 응답 DTO입니다.
 */
public record AnnouncementResponse(
    Long id,
    String title,
    String content,
    Long createdBy,
    LocalDateTime createdAt,
    Long modifiedBy,
    LocalDateTime modifiedAt
) {
    
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
