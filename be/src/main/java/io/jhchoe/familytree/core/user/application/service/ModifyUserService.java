package io.jhchoe.familytree.core.user.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.user.application.port.in.ModifyUserCommand;
import io.jhchoe.familytree.core.user.application.port.in.ModifyUserUseCase;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.application.port.out.ModifyUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.core.user.exception.UserExceptionCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User 프로필 수정 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class ModifyUserService implements ModifyUserUseCase {

    private final FindUserPort findUserPort;
    private final ModifyUserPort modifyUserPort;
    private final FindFamilyMemberPort findFamilyMemberPort;
    private final ModifyFamilyMemberPort modifyFamilyMemberPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User modify(ModifyUserCommand command) {
        Objects.requireNonNull(command, "command는 null일 수 없습니다");

        // 1. 기존 User 조회
        User existingUser = findUserPort.findById(command.userId())
            .orElseThrow(() -> new FTException(UserExceptionCode.USER_NOT_FOUND));

        // 2. User 프로필 수정
        User modifiedUser = existingUser.modifyProfile(
            command.name(),
            command.birthday(),
            command.birthdayType()
        );

        // 3. User 저장
        modifyUserPort.modify(modifiedUser);

        // 4. FamilyMember 생일 동기화 (이름은 동기화하지 않음)
        // TODO: 현재는 동기 처리로 트랜잭션 일관성 보장. 멤버가 많아지면 성능 이슈 발생 가능.
        //       비동기 전환 시 정합성 유지 방안 (Outbox Pattern 등) 함께 검토 필요.
        syncFamilyMemberBirthday(command.userId(), command.birthday(), command.birthdayType());

        return modifiedUser;
    }

    /**
     * User의 생일이 수정되면 연관된 FamilyMember의 생일을 동기화합니다.
     * 이름은 동기화하지 않습니다 (BR-4).
     * 동기화 가능 여부는 FamilyMember 객체가 스스로 판단합니다.
     *
     * @param userId       User ID
     * @param birthday     새 생일
     * @param birthdayType 새 생일 유형
     */
    private void syncFamilyMemberBirthday(
        Long userId,
        LocalDateTime birthday,
        BirthdayType birthdayType
    ) {
        // userId로 조회하면 수동 등록 멤버(userId=null)는 자동 제외됨
        List<FamilyMember> familyMembers = findFamilyMemberPort.findByUserId(userId);

        for (FamilyMember member : familyMembers) {
            // Tell, Don't Ask: 객체에게 동기화 요청, 객체가 스스로 가능 여부 판단
            member.syncBirthday(birthday, birthdayType)
                .ifPresent(modifyFamilyMemberPort::modify);
        }
    }
}