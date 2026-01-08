package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import java.util.List;

/**
 * Family 구성원 조회를 위한 UseCase 인터페이스입니다.
 * 단건 조회와 목록 조회를 모두 지원합니다.
 */
public interface FindFamilyMemberUseCase {

    /**
     * 특정 Family 구성원을 조회합니다.
     * 권한에 따른 필터링이 적용됩니다.
     *
     * @param query 단건 조회 조건을 담은 쿼리 객체
     * @return 조회된 Family 구성원
     * @throws IllegalArgumentException query가 null이거나 유효하지 않은 경우
     */
    FamilyMember find(FindFamilyMemberByIdQuery query);

    /**
     * Family 구성원 목록을 조회합니다.
     * 일반 사용자는 ACTIVE 상태 구성원만, ADMIN 이상은 SUSPENDED 포함 모든 구성원을 조회합니다.
     * 결과는 나이순(어린 순서)으로 정렬되며, 생일 정보가 없는 구성원은 맨 뒤에 표시됩니다.
     *
     * @param query 복수 조회 조건을 담은 쿼리 객체
     * @return 나이순으로 정렬된 Family 구성원 목록
     * @throws IllegalArgumentException query가 null이거나 유효하지 않은 경우
     */
    List<FamilyMember> findAll(FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query);

    /**
     * Family 구성원 목록을 태그 정보와 함께 조회합니다.
     * 각 멤버에 할당된 태그 목록이 함께 반환됩니다.
     *
     * @param query 태그 포함 복수 조회 조건을 담은 쿼리 객체
     * @return 태그 정보가 포함된 Family 구성원 목록
     * @throws IllegalArgumentException query가 null이거나 유효하지 않은 경우
     */
    List<FamilyMemberWithTagsInfo> findAll(FindFamilyMembersWithTagsQuery query);
}
