package io.jhchoe.familytree.core.family.application.port.in;

import lombok.Getter;

/**
 * 공지사항 조회를 위한 쿼리 객체입니다.
 */
@Getter
public class FindAnnouncementQuery {
    
    private final Long familyId;
    private final Long currentUserId;
    private final int page;
    private final int size;
    
    /**
     * 공지사항 조회 쿼리 객체를 생성합니다.
     *
     * @param familyId      Family ID
     * @param currentUserId 현재 로그인한 사용자 ID
     * @param page          페이지 번호 (0부터 시작)
     * @param size          페이지 크기
     */
    public FindAnnouncementQuery(
        Long familyId,
        Long currentUserId,
        int page,
        int size
    ) {
        validateFamilyId(familyId);
        validateCurrentUserId(currentUserId);
        validatePaging(page, size);
        
        this.familyId = familyId;
        this.currentUserId = currentUserId;
        this.page = page;
        this.size = size;
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
    
    private void validatePaging(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("페이지 크기는 1에서 100 사이여야 합니다.");
        }
    }
}
