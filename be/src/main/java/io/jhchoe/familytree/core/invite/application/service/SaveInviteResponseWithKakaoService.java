package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.auth.domain.KakaoUserInfo;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoCommand;
import io.jhchoe.familytree.core.invite.application.port.in.SaveInviteResponseWithKakaoUseCase;
import io.jhchoe.familytree.core.invite.application.port.out.FindFamilyInvitePort;
import io.jhchoe.familytree.core.invite.application.port.out.FindKakaoProfilePort;
import io.jhchoe.familytree.core.invite.application.port.out.ModifyFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    private final FindKakaoProfilePort findKakaoProfilePort;
    private final FindFamilyMemberPort findFamilyMemberPort;
    private final SaveFamilyMemberPort saveFamilyMemberPort;
    
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
        
        // 4. 카카오 프로필 조회
        KakaoUserInfo kakaoProfile = findKakaoProfilePort.findProfile(
            command.getKakaoAuthCode(),
            command.getRedirectUri()
        );
        
        log.info("카카오 프로필 조회 성공: kakaoId={}, name={}", 
            kakaoProfile.getId(), kakaoProfile.getName());
        
        // 5. 초대를 생성한 사용자의 Family 찾기
        // requesterId로 해당 사용자가 속한 Family를 찾음
        List<FamilyMember> requesterMembers = findFamilyMemberPort.findByUserId(invite.getRequesterId());
        if (requesterMembers.isEmpty()) {
            throw new FTException(InviteExceptionCode.REQUESTER_HAS_NO_FAMILY);
        }
        
        // 사용자는 하나의 Family에만 속한다고 가정
        Long familyId = requesterMembers.get(0).getFamilyId();
        
        // 6. 이미 해당 카카오 ID로 가입한 멤버가 있는지 확인
        List<FamilyMember> existingMembers = findFamilyMemberPort.findByFamilyId(familyId);
        boolean alreadyExists = existingMembers.stream()
            .anyMatch(member -> kakaoProfile.getId().equals(member.getKakaoId()));
        
        if (alreadyExists) {
            throw new FTException(InviteExceptionCode.ALREADY_FAMILY_MEMBER);
        }
        
        // 7. FamilyMember 생성 (비회원)
        FamilyMember newMember = FamilyMember.newKakaoMember(
            familyId,
            kakaoProfile.getId(),
            kakaoProfile.getName(),
            kakaoProfile.getImageUrl()
        );
        
        // 8. FamilyMember 저장
        Long savedMemberId = saveFamilyMemberPort.save(newMember);
        
        // 9. 초대 링크 사용 횟수 증가
        FamilyInvite updatedInvite = invite.incrementUsedCount();
        modifyFamilyInvitePort.modify(updatedInvite);
        
        log.info("카카오 OAuth를 통한 초대 수락 완료: familyId={}, kakaoId={}, memberName={}, memberId={}", 
            familyId, kakaoProfile.getId(), kakaoProfile.getName(), savedMemberId);
        
        return savedMemberId;
    }
}