package io.jhchoe.familytree.core.family.application.port.in;

import java.util.Objects;

/**
 * 가족트리 조회에 필요한 입력 데이터를 담는 쿼리 객체입니다.
 */
public record FindFamilyTreeQuery(
    Long familyId,
    Long centerMemberId,
    Integer maxGenerations
) {
    /**
     * FindFamilyTreeQuery 객체를 생성합니다.
     *
     * @param familyId 조회할 가족의 ID
     * @param centerMemberId 트리의 중심이 될 구성원 ID (선택사항, null인 경우 로그인 사용자 기준)
     * @param maxGenerations 조회할 최대 세대 수 (선택사항, null인 경우 기본값 3)
     */
    public FindFamilyTreeQuery {
        Objects.requireNonNull(familyId, "familyId must not be null");
        
        if (maxGenerations != null && maxGenerations < 1) {
            throw new IllegalArgumentException("maxGenerations must be positive number");
        }
        
        if (maxGenerations != null && maxGenerations > 10) {
            throw new IllegalArgumentException("maxGenerations must not exceed 10");
        }
    }
    
    /**
     * 기본 설정으로 FindFamilyTreeQuery를 생성합니다.
     *
     * @param familyId 조회할 가족의 ID
     * @return 기본 설정이 적용된 쿼리 객체 (maxGenerations: 3)
     */
    public static FindFamilyTreeQuery withDefaults(Long familyId) {
        return new FindFamilyTreeQuery(familyId, null, 3);
    }
    
    /**
     * 중심 구성원을 지정하여 FindFamilyTreeQuery를 생성합니다.
     *
     * @param familyId 조회할 가족의 ID
     * @param centerMemberId 트리의 중심이 될 구성원 ID
     * @return 중심 구성원이 지정된 쿼리 객체 (maxGenerations: 3)
     */
    public static FindFamilyTreeQuery withCenterMember(Long familyId, Long centerMemberId) {
        return new FindFamilyTreeQuery(familyId, centerMemberId, 3);
    }
    
    /**
     * 최대 세대 수가 설정되지 않은 경우 기본값을 반환합니다.
     *
     * @return 최대 세대 수 (기본값: 3)
     */
    public int getMaxGenerationsOrDefault() {
        return maxGenerations != null ? maxGenerations : 3;
    }
}
