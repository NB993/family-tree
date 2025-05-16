package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.Family;
import java.util.List;

/**
 * Family 조회 기능을 정의하는 유스케이스 인터페이스.
 */
public interface FindFamilyUseCase {

    /**
     * Family 조회 요청을 처리합니다.
     *
     * @param id Family ID
     * @return 조회된 Family 정보
     */
    Family findById(Long id);

    /**
     * 지정된 이름으로 Family를 검색합니다.
     *
     * @param query Family 이름 검색에 필요한 입력 데이터를 포함하는 쿼리 객체
     * @return 조회된 Family 목록, 해당하는 Family가 없을 경우 빈 목록을 반환
     */
    List<Family> findByNameLike(FindFamilyByNameLikeQuery query);
}
