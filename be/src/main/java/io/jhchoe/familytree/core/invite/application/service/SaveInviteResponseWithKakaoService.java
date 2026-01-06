package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoCommand;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoUseCase;
import io.jhchoe.familytree.core.invite.application.port.out.FindFamilyInvitePort;
import io.jhchoe.familytree.core.invite.application.port.out.ModifyFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카카오 OAuth를 통한 초대 응답 저장 서비스입니다.
 * <p>
 * 초대 수락 시 kakaoId로 User를 조회하고, User 정보를 FamilyMember에 복사합니다.
 * (name, profileUrl, birthday는 일방향 복사 후 독립적으로 관리됨)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaveInviteResponseWithKakaoService implements SaveInviteResponseWithKakaoUseCase {

    private final FindFamilyInvitePort findFamilyInvitePort;
    private final ModifyFamilyInvitePort modifyFamilyInvitePort;
    private final FindFamilyMemberPort findFamilyMemberPort;
    private final SaveFamilyMemberPort saveFamilyMemberPort;
    private final FindUserPort findUserPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long save(SaveInviteResponseWithKakaoCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 1. 초대 링크 조회 및 유효성 검증
        FamilyInvite invite = findFamilyInvitePort.findByCode(command.getInviteCode())
            .orElseThrow(() -> new FTException(InviteExceptionCode.INVITE_NOT_FOUND));

        // 2. 초대 링크 만료 확인
        if (invite.isExpired()) {
            throw new FTException(InviteExceptionCode.INVITE_EXPIRED);
        }

        // 3. 초대 링크 사용 가능 횟수 확인
        if (invite.getMaxUses() != null && invite.getUsedCount() >= invite.getMaxUses()) {
            throw new FTException(InviteExceptionCode.INVITE_MAX_USES_EXCEEDED);
        }

        // 4. kakaoId로 User 조회 (OAuth2 로그인 시 이미 생성됨)
        User user = findUserPort.findByKakaoId(command.getKakaoId())
            .orElseThrow(() -> {
                log.error("User를 찾을 수 없음. kakaoId={}", command.getKakaoId());
                return new FTException(InviteExceptionCode.USER_NOT_FOUND);
            });

        Long userId = user.getId();
        log.info("User 조회 완료: userId={}, kakaoId={}", userId, command.getKakaoId());

        // 5. 초대에서 familyId 직접 가져옴
        Long familyId = invite.getFamilyId();

        // 6. 초대를 생성한 사용자의 해당 Family 멤버십 조회
        FamilyMember requesterMember = findFamilyMemberPort.findByFamilyIdAndUserId(familyId, invite.getRequesterId())
            .orElseThrow(() -> new FTException(InviteExceptionCode.REQUESTER_NOT_FAMILY_MEMBER));

        // 7. 자기 자신이 보낸 초대는 수락할 수 없음 (userId로 비교)
        if (userId.equals(requesterMember.getUserId())) {
            throw new FTException(InviteExceptionCode.CANNOT_ACCEPT_OWN_INVITE);
        }

        // 8. 이미 해당 userId로 가입한 멤버가 있는지 확인
        boolean alreadyExists = findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, userId);
        if (alreadyExists) {
            throw new FTException(InviteExceptionCode.ALREADY_FAMILY_MEMBER);
        }

        // 9. FamilyMember 생성 (User 정보를 복사)
        FamilyMember newMember = FamilyMember.newMember(
            familyId,
            userId,
            user.getName(),          // User에서 복사
            user.getProfileUrl(),    // User에서 복사
            user.getBirthday(),      // User에서 복사
            user.getBirthdayType()   // User에서 복사
        );

        // 10. FamilyMember 저장
        Long savedMemberId = saveFamilyMemberPort.save(newMember);

        // 11. 초대 링크 사용 횟수 증가 및 완료 처리
        FamilyInvite updatedInvite = invite.incrementUsedCount();

        // 최대 사용 횟수 도달 시 상태를 COMPLETED로 변경
        if (updatedInvite.getMaxUses() != null &&
            updatedInvite.getUsedCount() >= updatedInvite.getMaxUses()) {
            updatedInvite = updatedInvite.complete();
            log.info("초대 링크 최대 사용 횟수 도달. 상태 COMPLETED로 변경: inviteCode={}",
                invite.getInviteCode());
        }

        modifyFamilyInvitePort.modify(updatedInvite);

        log.info("카카오 OAuth를 통한 초대 수락 완료: familyId={}, userId={}, memberName={}, memberId={}",
            familyId, userId, user.getName(), savedMemberId);

        return savedMemberId;
    }
}
