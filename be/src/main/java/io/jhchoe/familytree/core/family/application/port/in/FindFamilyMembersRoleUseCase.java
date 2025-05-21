package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import java.util.List;

/**
 * Family 구성원의 역할 정보를 조회하기 위한 유스케이스입니다.
 */
public interface FindFamilyMembersRoleUseCase {

    /**
     * 특정 Family의 모든 구성원과 역할 정보를 조회합니다.
     *
     * @param query 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 구성원 목록
     */
    List<FamilyMember> findAllByFamilyId(FindFamilyMembersRoleQuery query);
}
