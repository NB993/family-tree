package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByNameLikeQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindFamilyService implements FindFamilyUseCase {

    private final FindFamilyPort findFamilyPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public Family findById(Long id) {
        if (id == null) {
            throw FTException.MISSING_PARAMETER;
        }

        return findFamilyPort.findById(id)
            .orElseThrow(() -> FTException.NOT_FOUND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Family> findByNameLike(FindFamilyByNameLikeQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return findFamilyPort.findByNameLike(query.getName());
    }
}
