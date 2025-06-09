package io.jhchoe.familytree.core.family.application.port.out;

import io.jhchoe.familytree.core.family.domain.Family;
import java.util.List;
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

    boolean existsById(Long id);

    /**
     * 입력된 name을 포함하는 Family 엔티티를 조회합니다.
     *
     * @param name 조회할 Family이 포함할 이름
     * @return 조회된 Family 목록
     */
    List<Family> findByNameContaining(String name);

    /**
     * 가족명으로 Family를 조회합니다.
     * 소프트 딜리트된 Family는 조회 결과에서 제외됩니다.
     *
     * @param name 가족명 (정확히 일치하는 이름)
     * @return Family 조회 결과, 존재하지 않거나 삭제된 경우 빈 Optional 반환
     */
    Optional<Family> findByName(String name);
}
