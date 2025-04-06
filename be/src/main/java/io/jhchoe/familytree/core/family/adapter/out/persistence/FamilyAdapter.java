package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.core.family.application.port.out.CreateFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Family 데이터를 저장하기 위한 어댑터(Adapter) 클래스입니다. 이 클래스는 CreateFamilyPort 인터페이스를 구현합니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyAdapter implements CreateFamilyPort {

    private final FamilyJpaRepository familyJpaRepository;

    /**
     * Family 데이터를 저장하고, 저장된 Family의 ID를 반환합니다.
     *
     * @param family 저장할 Family 데이터 (null 불가)
     * @return 저장된 Family의 ID
     * @throws NullPointerException Family 데이터가 null인 경우 예외가 발생합니다.
     */
    @Override
    public Long create(Family family) {
        Objects.requireNonNull(family, "family must not be null");

        FamilyEntity familyEntity = FamilyEntity.from(family);
        return familyJpaRepository.save(familyEntity).getId();
    }
}
