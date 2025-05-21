package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatusHistory;
import java.util.List;

/**
 * 구성원 상태 변경 이력 조회를 위한 포트입니다.
 */
public interface FindFamilyMemberStatusHistoryPort {

    /**
     * 특정 구성원의 모든 상태 변경 이력을 조회합니다.
     *
     * @param memberId 구성원 ID
     * @return 상태 변경 이력 목록
     */
    List<FamilyMemberStatusHistory> findAllByMemberId(Long memberId);

    /**
     * 특정 가족의 모든 상태 변경 이력을 조회합니다.
     *
     * @param familyId 가족 ID
     * @return 상태 변경 이력 목록
     */
    List<FamilyMemberStatusHistory> findAllByFamilyId(Long familyId);
}
