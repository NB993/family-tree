package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoCommand;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoUseCase;
import io.jhchoe.familytree.core.invite.application.port.out.FindFamilyInvitePort;
import io.jhchoe.familytree.core.invite.application.port.out.ModifyFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카카오 OAuth를 통한 초대 응답 저장 서비스입니다.
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

        // 4. email로 User 확인
        String email = command.getEmail();
        Long userId = null;

        Optional<User> existingUser = findUserPort.findByEmail(email);
        if (existingUser.isPresent()) {
            userId = existingUser.get().getId();
            log.info("기존 User 발견: userId={}, email={}", userId, email);
        } else {
            log.info("User 없음. kakaoId만 저장: email={}", email);
        }

        // 5. 초대를 생성한 사용자의 Family 찾기
        // requesterId로 해당 사용자가 속한 Family를 찾음
        List<FamilyMember> requesterMembers = findFamilyMemberPort.findByUserId(invite.getRequesterId());
        if (requesterMembers.isEmpty()) {
            throw new FTException(InviteExceptionCode.REQUESTER_HAS_NO_FAMILY);
        }
        
        // 사용자는 하나의 Family에만 속한다고 가정
        FamilyMember requesterMember = requesterMembers.get(0);
        Long familyId = requesterMember.getFamilyId();

        // 6. 초대를 보낸 사람이 OWNER인지 확인 (OWNER는 자기 초대를 수락할 수 없음)
        if (requesterMember.getRole() == FamilyMemberRole.OWNER) {
            throw new FTException(InviteExceptionCode.CANNOT_ACCEPT_OWN_INVITE);
        }

        // 7. 이미 해당 카카오 ID로 가입한 멤버가 있는지 확인
        List<FamilyMember> existingMembers = findFamilyMemberPort.findByFamilyId(familyId);
        boolean alreadyExists = existingMembers.stream()
            .anyMatch(member -> command.getKakaoId().equals(member.getKakaoId()));

        if (alreadyExists) {
            throw new FTException(InviteExceptionCode.ALREADY_FAMILY_MEMBER);
        }

        // 8. FamilyMember 생성
        LocalDateTime fixedBirthday = LocalDateTime.of(1990, 1, 1, 0, 0);

        FamilyMember newMember = FamilyMember.newKakaoMember(
            familyId,
            userId,              // User 있으면 userId, 없으면 null
            command.getKakaoId(),
            command.getName(),
            command.getProfileUrl(),
            fixedBirthday        // 임의 고정 값
        );

        // 9. FamilyMember 저장
        Long savedMemberId = saveFamilyMemberPort.save(newMember);

        // 10. 초대 링크 사용 횟수 증가 및 완료 처리
        FamilyInvite updatedInvite = invite.incrementUsedCount();

        // 최대 사용 횟수 도달 시 상태를 COMPLETED로 변경
        if (updatedInvite.getMaxUses() != null &&
            updatedInvite.getUsedCount() >= updatedInvite.getMaxUses()) {
            updatedInvite = updatedInvite.complete();
            log.info("초대 링크 최대 사용 횟수 도달. 상태 COMPLETED로 변경: inviteCode={}",
                invite.getInviteCode());
        }

        modifyFamilyInvitePort.modify(updatedInvite);

        log.info("카카오 OAuth를 통한 초대 수락 완료: familyId={}, userId={}, kakaoId={}, memberName={}, memberId={}",
            familyId, userId, command.getKakaoId(), command.getName(), savedMemberId);

        return savedMemberId;
    }
}
