package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.Family;
import java.util.List;

/**
 * Family 조회 기능을 정의하는 유스케이스 인터페이스.
 * 
 * <p>헥사고날 아키텍처의 입력 포트로서 Family 도메인의 조회 기능을 정의합니다.
 * 모든 조회 메서드는 Query 객체를 파라미터로 받는 표준 패턴을 따릅니다.</p>
 */
public interface FindFamilyUseCase {

    /**
     * Family ID로 단일 Family를 조회합니다.
     *
     * @param query Family ID를 포함하는 쿼리 객체
     * @return 조회된 Family 정보
     * @throws io.jhchoe.familytree.common.exception.FTException Family를 찾을 수 없는 경우
     */
    Family find(FindFamilyByIdQuery query);

    /**
     * 이름으로 Family 목록을 조회합니다.
     *
     * @param query Family 이름 검색에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 Family 목록, 해당하는 Family가 없을 경우 빈 목록을 반환
     */
    List<Family> findAll(FindFamilyByNameContainingQuery query);
}
