package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Optional;

/**
 * Family 엔티티를 조회하기 위한 포트 인터페이스입니다.
 */
public interface FindFamilyPort {

    /**
     * 지정된 ID에 해당하는 Family 엔티티를 조회합니다.
     *
     * @param id 조회할 Family의 고유 식별자
     * @return 조회된 Family 객체를 포함하는 Optional, 존재하지 않는 경우 빈 Optional 반환
     */
    Optional<Family> findById(Long id);
}
