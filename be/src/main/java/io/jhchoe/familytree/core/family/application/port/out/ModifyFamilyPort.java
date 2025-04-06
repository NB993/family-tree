package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Family;

/**
 * Family 엔티티를 수정하기 위한 포트 인터페이스입니다.
 */
public interface ModifyFamilyPort {


    /**
     * Family 데이터를 수정하고 수정된 Family의 ID를 반환합니다.
     *
     * @param family 수정할 Family 객체 (null 불가)
     * @return 수정된 Family의 ID
     */
    Long modifyFamily(Family family);
}
