package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import java.util.List;
import java.util.Optional;

/**
 * 가족트리 조회를 위한 아웃바운드 포트입니다.
 * 트리 구성에 필요한 모든 데이터를 조회하는 기능을 제공합니다.
 */
public interface FindFamilyTreePort {

    /**
     * 가족 정보를 조회합니다.
     *
     * @param familyId 조회할 가족 ID
     * @return 가족 정보, 존재하지 않으면 빈 Optional
     */
    Optional<Family> findFamily(Long familyId);

    /**
     * 가족의 모든 활성 구성원을 조회합니다.
     *
     * @param familyId 가족 ID
     * @return 활성 구성원 목록
     */
    List<FamilyMember> findActiveFamilyMembers(Long familyId);

    /**
     * 특정 구성원의 정보를 조회합니다.
     *
     * @param memberId 구성원 ID
     * @return 구성원 정보, 존재하지 않으면 빈 Optional
     */
    Optional<FamilyMember> findFamilyMember(Long memberId);

    /**
     * 가족의 모든 구성원 관계를 조회합니다.
     *
     * @param familyId 가족 ID
     * @return 구성원 관계 목록
     */
    List<FamilyMemberRelationship> findFamilyMemberRelationships(Long familyId);

    /**
     * 특정 구성원이 정의한 관계들을 조회합니다.
     *
     * @param fromMemberId 관계를 정의한 구성원 ID
     * @return 해당 구성원이 정의한 관계 목록
     */
    List<FamilyMemberRelationship> findRelationshipsByFromMember(Long fromMemberId);

    /**
     * 현재 사용자가 해당 가족의 구성원인지 확인합니다.
     *
     * @param familyId 가족 ID
     * @param userId 사용자 ID
     * @return 해당 사용자의 가족 구성원 정보, 구성원이 아니면 빈 Optional
     */
    Optional<FamilyMember> findFamilyMemberByUserId(Long familyId, Long userId);
}
