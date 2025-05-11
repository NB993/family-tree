package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.Family;

public interface FindFamilyUseCase {

    Family findById(Long id);
}
