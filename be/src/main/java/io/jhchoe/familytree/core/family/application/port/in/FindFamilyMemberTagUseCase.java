package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import java.util.List;

/**
 * 태그 조회를 위한 유스케이스 인터페이스입니다.
 */
public interface FindFamilyMemberTagUseCase {

    /**
     * Family의 모든 태그를 조회합니다.
     * 태그는 가나다순(이름 오름차순)으로 정렬됩니다.
     *
     * @param query         태그 조회에 필요한 정보를 담은 쿼리 객체
     * @param currentUserId 현재 사용자 ID
     * @return 태그 목록 (각 태그에 할당된 멤버 수 포함)
     * @throws FTException 가족이 존재하지 않거나, 가족 구성원이 아닌 경우
     */
    List<FamilyMemberTagInfo> findAll(FindFamilyMemberTagsQuery query, Long currentUserId);
}
