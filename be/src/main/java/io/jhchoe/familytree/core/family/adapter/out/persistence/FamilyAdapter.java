package io.jhchoe.familytree.core.family.adapter.out.persistence;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Family 아웃바운드 어댑터 클래스입니다. 이 클래스는 Family와 관련된 모든 outbound port를 구현합니다.
 */
@Component
@RequiredArgsConstructor
public class FamilyAdapter implements SaveFamilyPort, ModifyFamilyPort, FindFamilyPort {

    private final FamilyJpaRepository familyJpaRepository;

    /**
     * Family 데이터를 저장하고, 저장된 Family의 ID를 반환합니다.
     *
     * @param family 저장할 Family 데이터 (null 불가)
     * @return 저장된 Family의 ID
     * @throws NullPointerException Family 데이터가 null인 경우 예외가 발생합니다.
     */
    @Override
    public Long save(Family family) {
        Objects.requireNonNull(family, "family must not be null");

        FamilyJpaEntity familyJpaEntity = FamilyJpaEntity.from(family);
        return familyJpaRepository.save(familyJpaEntity).getId();
    }

    @Override
    public Optional<Family> findById(Long id) {
        return familyJpaRepository.findById(id).map(FamilyJpaEntity::toFamily);
    }

    /**
     * Family 데이터를 수정하고 저장된 Family의 ID를 반환합니다.
     *
     * @param family 수정할 Family 데이터 (null 불가)
     * @return 수정된 Family의 ID
     * @throws NullPointerException Family 데이터가 null인 경우 예외가 발생합니다.
     * @throws FTException 지정된 ID에 해당하는 Family가 없을 경우 예외가 발생합니다.
     */
    @Override
    public Long modifyFamily(Family family) {
        Objects.requireNonNull(family, "family must not be null");

        return familyJpaRepository.findById(family.getId())
            .map(familyJpaEntity -> {
                familyJpaEntity.update(family.getName(), family.getDescription(), family.getProfileUrl());
                FamilyJpaEntity save = familyJpaRepository.save(familyJpaEntity);
                return save.getId();
            })
            .orElseThrow(() -> new FTException(CommonExceptionCode.NOT_FOUND, "family"));
    }
}
