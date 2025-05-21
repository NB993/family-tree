package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * 공지사항 저장을 위한 커맨드 객체입니다.
 */
@Getter
public class SaveAnnouncementCommand {
    
    private final Long familyId;
    private final Long currentUserId;
    private final String title;
    private final String content;
    
    /**
     * 공지사항 저장 커맨드 객체를 생성합니다.
     *
     * @param familyId      Family ID
     * @param currentUserId 현재 로그인한 사용자 ID
     * @param title         제목
     * @param content       내용
     */
    public SaveAnnouncementCommand(
        Long familyId,
        Long currentUserId,
        String title,
        String content
    ) {
        validateFamilyId(familyId);
        validateCurrentUserId(currentUserId);
        validateTitle(title);
        
        this.familyId = familyId;
        this.currentUserId = currentUserId;
        this.title = title;
        this.content = content;
    }
    
    private void validateFamilyId(Long familyId) {
        if (familyId == null || familyId <= 0) {
            throw new IllegalArgumentException("유효한 Family ID가 필요합니다.");
        }
    }
    
    private void validateCurrentUserId(Long currentUserId) {
        if (currentUserId == null || currentUserId <= 0) {
            throw new IllegalArgumentException("유효한 현재 사용자 ID가 필요합니다.");
        }
    }
    
    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("공지사항 제목은 필수입니다.");
        }
    }
}
