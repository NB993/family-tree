package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.auth.exception.AuthExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyUseCase;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Family 관련 비즈니스 로직을 처리하는 서비스 클래스입니다. 
 * 이 클래스는 Family 엔터티를 생성하고 저장하는 역할을 수행하며, 
 * Family 생성 시 생성자에게 OWNER 권한을 자동으로 부여합니다.
 */
@Service
@RequiredArgsConstructor
public class SaveFamilyService implements SaveFamilyUseCase {

    private final SaveFamilyPort saveFamilyPort;
    private final SaveFamilyMemberPort saveFamilyMemberPort;
    private final FindUserPort findUserPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(final SaveFamilyCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. 사용자 정보 조회
        User user = findUserPort.findById(command.getUserId())
            .orElseThrow(() -> new FTException(AuthExceptionCode.USER_NOT_FOUND));

        // 2. Family 생성 및 저장
        Family family = Family.newFamily(
            command.getName(), 
            command.getDescription(), 
            command.getProfileUrl(), 
            command.getIsPublic()
        );
        Long familyId = saveFamilyPort.save(family);

        // 3. Family 생성자에게 OWNER 권한 자동 부여
        FamilyMember owner = FamilyMember.newOwner(
            familyId,
            command.getUserId(),
            user.getName(),        // 사용자의 실제 이름 사용
            user.getProfileUrl(),  // 사용자의 프로필 URL 사용
            null,                  // 생일은 선택사항 (추후 확장 가능)
            null                   // 국적은 선택사항 (추후 확장 가능)
        );
        saveFamilyMemberPort.save(owner);

        return familyId;
    }
}
