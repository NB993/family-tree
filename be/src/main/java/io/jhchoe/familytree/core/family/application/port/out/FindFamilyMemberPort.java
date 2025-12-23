package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import java.util.List;
import java.util.Optional;

/**
 * Family 구성원 정보를 조회하기 위한 포트입니다.
 */
public interface FindFamilyMemberPort {

    /**
     * 특정 Family와 사용자 ID로 구성원 존재 여부를 확인합니다.
     *
     * @param familyId Family ID
     * @param userId 사용자 ID
     * @return 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByFamilyIdAndUserId(Long familyId, Long userId);

    /**
     * 특정 사용자의 활성 Family 구성원 수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 활성 Family 구성원 수
     */
    int countActiveByUserId(Long userId);

    /**
     * 지정된 ID에 해당하는 Family 구성원을 조회합니다.
     *
     * @param id 조회할 FamilyMember ID
     * @return 조회된 FamilyMember 객체를 포함하는 Optional, 존재하지 않는 경우 빈 Optional 반환
     */
    Optional<FamilyMember> findById(Long id);
    
    /**
     * 특정 Family의 모든 구성원을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @return FamilyMember 객체 목록
     */
    List<FamilyMember> findAllByFamilyId(Long familyId);
    
    /**
     * 현재 사용자가 특정 Family의 구성원인지 확인하고, 구성원 정보를 조회합니다.
     *
     * @param familyId Family ID
     * @param userId 사용자 ID
     * @return 조회된 FamilyMember 객체를 포함하는 Optional, 존재하지 않는 경우 빈 Optional 반환
     */
    Optional<FamilyMember> findByFamilyIdAndUserId(Long familyId, Long userId);

    /**
     * 특정 사용자가 소속된 모든 Family의 구성원 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 소속된 FamilyMember 객체 목록
     */
    List<FamilyMember> findAllByUserId(Long userId);

    /**
     * 특정 사용자가 소속된 모든 Family의 구성원 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 소속된 FamilyMember 객체 목록
     */
    List<FamilyMember> findByUserId(Long userId);

    /**
     * 특정 Family의 모든 구성원을 조회합니다.
     *
     * @param familyId 조회할 Family ID
     * @return FamilyMember 객체 목록
     */
    List<FamilyMember> findByFamilyId(Long familyId);

    /**
     * 특정 사용자의 특정 역할을 가진 구성원 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param role 역할
     * @return 조회된 FamilyMember 객체를 포함하는 Optional, 존재하지 않는 경우 빈 Optional 반환
     */
    Optional<FamilyMember> findByUserIdAndRole(Long userId, FamilyMemberRole role);
}
