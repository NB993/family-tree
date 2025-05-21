package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatusHistory;

/**
 * 구성원 상태 변경 이력 저장을 위한 포트입니다.
 */
public interface SaveFamilyMemberStatusHistoryPort {

    /**
     * 구성원 상태 변경 이력을 저장합니다.
     *
     * @param history 저장할 상태 변경 이력
     * @return 저장된 이력의 ID
     */
    Long save(FamilyMemberStatusHistory history);
}
