package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTagMapping;
import java.util.List;

/**
 * 태그 매핑 조회를 위한 아웃바운드 포트입니다.
 */
public interface FindFamilyMemberTagMappingPort {

    /**
     * 특정 멤버의 모든 태그 매핑을 조회합니다.
     *
     * @param memberId 멤버 ID
     * @return 태그 매핑 목록
     */
    List<FamilyMemberTagMapping> findAllByMemberId(Long memberId);

    /**
     * 특정 태그에 매핑된 멤버 수를 조회합니다.
     *
     * @param tagId 태그 ID
     * @return 멤버 수
     */
    int countByTagId(Long tagId);
}
