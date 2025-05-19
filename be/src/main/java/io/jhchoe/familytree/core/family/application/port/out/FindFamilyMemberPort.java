package io.jhchoe.familytree.core.family.application.port.out;

/**
 * Family 멤버 조회를 위한 포트 인터페이스입니다.
 */
public interface FindFamilyMemberPort {

    /**
     * 특정 사용자가 Family에 이미 가입되어 있는지 확인합니다.
     *
     * @param familyId 확인할 Family ID
     * @param userId 확인할 사용자 ID
     * @return 가입되어 있으면 true, 아니면 false
     */
    boolean existsByFamilyIdAndUserId(Long familyId, Long userId);
    
    /**
     * 특정 사용자가 활성 상태로 가입한 Family의 수를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 활성 상태로 가입한 Family 수
     */
    int countActiveByUserId(Long userId);
}
