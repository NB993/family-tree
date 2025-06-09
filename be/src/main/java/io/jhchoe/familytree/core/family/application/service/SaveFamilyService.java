package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * Family 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. 이 클래스는 Family 엔터티를 생성하고 저장하는 역할을 수행합니다.
 */
@Service
@RequiredArgsConstructor
public class SaveFamilyService implements SaveFamilyUseCase {

    private final SaveFamilyPort saveFamilyPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long save(SaveFamilyCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Family family = Family.newFamily(command.getName(), command.getDescription(), command.getProfileUrl(), command.getIsPublic());
        return saveFamilyPort.save(family);
    }
}
