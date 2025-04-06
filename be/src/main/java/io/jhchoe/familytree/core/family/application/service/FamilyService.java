package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.application.port.in.CreateFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.CreateFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.out.CreateFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * Family 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. 이 클래스는 Family 엔터티를 생성하고 저장하는 역할을 수행합니다.
 */
@Service
@RequiredArgsConstructor
public class FamilyService implements CreateFamilyUseCase {

    private final CreateFamilyPort createFamilyPort;

    /**
     * 주어진 CreateFamilyCommand를 기반으로 새로운 Family를 생성하고 저장합니다.
     *
     * @param command Family 생성을 위한 명령 객체 (null 불가)
     * @return 생성된 Family의 ID
     * @throws NullPointerException command가 null인 경우 예외가 발생합니다.
     */
    @Override
    public Long create(CreateFamilyCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Family family = Family.newFamily(command.getName(), command.getDescription(), command.getProfileUrl());
        return createFamilyPort.create(family);
    }
}
