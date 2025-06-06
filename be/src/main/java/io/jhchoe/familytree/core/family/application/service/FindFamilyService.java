package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByNameContainingQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Family 조회 기능을 구현하는 서비스 클래스.
 * 
 * <p>헥사고날 아키텍처의 애플리케이션 계층에서 Family 조회 비즈니스 로직을 담당합니다.
 * 코어 계층에서는 {@code Objects.requireNonNull}을 사용하여 null 체크를 수행합니다.</p>
 */
@Service
@RequiredArgsConstructor
public class FindFamilyService implements FindFamilyUseCase {

    private final FindFamilyPort findFamilyPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public Family find(FindFamilyByIdQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyPort.findById(query.id())
            .orElseThrow(() -> FTException.NOT_FOUND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Family> findAll(FindFamilyByNameContainingQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyPort.findByNameContaining(query.getName());
    }
}
