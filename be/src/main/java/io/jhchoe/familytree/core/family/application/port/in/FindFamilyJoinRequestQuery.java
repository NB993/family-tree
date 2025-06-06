package io.jhchoe.familytree.core.family.application.port.in;

import java.util.Objects;
import lombok.Getter;

/**
 * Family 가입 신청 목록 조회를 위한 쿼리 클래스입니다.
 */
@Getter
public class FindFamilyJoinRequestQuery {
    
    private final Long familyId;
    private final Long currentUserId;
    
    /**
     * 가입 신청 목록 조회 쿼리 객체를 생성합니다.
     *
     * @param familyId      Family ID
     * @param currentUserId 현재 요청하는 사용자 ID
     */
    public FindFamilyJoinRequestQuery(Long familyId, Long currentUserId) {
        this.familyId = Objects.requireNonNull(familyId, "familyId must not be null");
        this.currentUserId = Objects.requireNonNull(currentUserId, "currentUserId must not be null");
        
        validate();
    }
    
    /**
     * 쿼리 객체의 유효성을 검증합니다.
     */
    private void validate() {
        if (familyId <= 0) {
            throw new IllegalStateException("familyId must be positive");
        }
        
        if (currentUserId <= 0) {
            throw new IllegalStateException("currentUserId must be positive");
        }
    }
}
