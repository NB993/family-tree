package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Family;

/**
 * CreateFamilyPort는 Family 정보를 생성하는 데 사용되는 포트입니다.
 */
public interface CreateFamilyPort {

    /**
     * 지정된 Family 정보를 기반으로 새 가족을 생성합니다.
     *
     * @param family 생성할 Family 정보를 담고 있는 객체
     * @return 생성된 Family의 ID
     */
    Long create(Family family);
}
