package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * 공지사항 삭제를 위한 커맨드 객체입니다.
 */
@Getter
public class DeleteAnnouncementCommand {
    
    private final Long id;
    private final Long familyId;
    private final Long currentUserId;
    
    /**
     * 공지사항 삭제 커맨드 객체를 생성합니다.
     *
     * @param id            공지사항 ID
     * @param familyId      Family ID
     * @param currentUserId 현재 로그인한 사용자 ID
     */
    public DeleteAnnouncementCommand(
        Long id,
        Long familyId,
        Long currentUserId
    ) {
        validateId(id);
        validateFamilyId(familyId);
        validateCurrentUserId(currentUserId);
        
        this.id = id;
        this.familyId = familyId;
        this.currentUserId = currentUserId;
    }
    
    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("유효한 공지사항 ID가 필요합니다.");
        }
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
}
