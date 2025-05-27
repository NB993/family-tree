package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyTree;

/**
 * 가족트리 조회를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface FindFamilyTreeUseCase {

    /**
     * 지정된 조건으로 가족트리를 조회합니다.
     * 
     * @param query 가족트리 조회에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 가족트리 객체
     * @throws io.jhchoe.familytree.common.exception.FTException 가족이 존재하지 않거나 중심 구성원이 해당 가족에 속하지 않는 경우
     */
    FamilyTree findFamilyTree(FindFamilyTreeQuery query);
}
