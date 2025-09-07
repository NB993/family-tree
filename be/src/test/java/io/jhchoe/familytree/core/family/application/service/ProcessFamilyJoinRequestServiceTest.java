package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ProcessFamilyJoinRequestCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * [Unit Test] ProcessFamilyJoinRequestServiceTest
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] ProcessFamilyJoinRequestServiceTest")
class ProcessFamilyJoinRequestServiceTest {

    @InjectMocks
    private ProcessFamilyJoinRequestService processService;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private FindFamilyJoinRequestPort findFamilyJoinRequestPort;

    @Mock
    private ModifyFamilyJoinRequestPort modifyFamilyJoinRequestPort;

    @Mock
    private SaveFamilyMemberPort saveFamilyMemberPort;

    @Test
    @DisplayName("OWNER 권한으로 가입 신청을 승인할 때 성공합니다")
    void process_success_when_owner_approves_request() {
        // given
        Long familyId = 1L;
        Long requestId = 2L;
        Long currentUserId = 3L;
        Long requesterId = 4L;
        
        ProcessFamilyJoinRequestCommand command = new ProcessFamilyJoinRequestCommand(
            familyId, requestId, FamilyJoinRequestStatus.APPROVED, "승인", currentUserId
        );

        // 현재 사용자가 OWNER 권한을 가진 구성원
        FamilyMember ownerMember = FamilyMember.withId(
            1L, familyId, currentUserId, "소유자", "profile.jpg",
            LocalDateTime.now(), "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            currentUserId, LocalDateTime.now(), currentUserId, LocalDateTime.now()
        );

        // 처리할 가입 신청
        FamilyJoinRequest pendingRequest = FamilyJoinRequest.withId(
            requestId, familyId, requesterId, FamilyJoinRequestStatus.PENDING,
            LocalDateTime.now(), requesterId, LocalDateTime.now(), requesterId
        );

        // 승인 처리된 가입 신청
        FamilyJoinRequest approvedRequest = pendingRequest.approve();

        // Mocking: 현재 사용자가 Family 구성원인지 확인
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(ownerMember));

        // Mocking: 가입 신청 조회
        when(findFamilyJoinRequestPort.findById(requestId))
            .thenReturn(Optional.of(pendingRequest));

        // Mocking: FamilyMember 저장
        when(saveFamilyMemberPort.save(any(FamilyMember.class)))
            .thenReturn(5L);

        // Mocking: 가입 신청 상태 업데이트
        when(modifyFamilyJoinRequestPort.updateFamilyJoinRequest(any(FamilyJoinRequest.class)))
            .thenReturn(approvedRequest);

        // when
        FamilyJoinRequest result = processService.process(command);

        // then
        assertThat(result.getStatus()).isEqualTo(FamilyJoinRequestStatus.APPROVED);
        // modifiedBy는 JPA Audit을 통해 자동 설정되므로 도메인에서는 null로 반환

        // FamilyMember 저장이 호출되었는지 확인
        verify(saveFamilyMemberPort).save(any(FamilyMember.class));

        // 저장된 FamilyMember 검증
        ArgumentCaptor<FamilyMember> memberCaptor = ArgumentCaptor.forClass(FamilyMember.class);
        verify(saveFamilyMemberPort).save(memberCaptor.capture());
        FamilyMember savedMember = memberCaptor.getValue();
        assertThat(savedMember.getFamilyId()).isEqualTo(familyId);
        assertThat(savedMember.getUserId()).isEqualTo(requesterId);
        assertThat(savedMember.getRole()).isEqualTo(FamilyMemberRole.MEMBER);
    }

    // @Test
    @DisplayName("MEMBER 권한으로 처리 시도하면 예외가 발생합니다")
    void throw_exception_when_user_has_member_role() {
        // given
        Long familyId = 1L;
        Long requestId = 2L;
        Long currentUserId = 3L;
        
        ProcessFamilyJoinRequestCommand command = new ProcessFamilyJoinRequestCommand(
            familyId, requestId, FamilyJoinRequestStatus.APPROVED, "승인", currentUserId
        );

        // 현재 사용자가 MEMBER 권한을 가진 구성원
        FamilyMember memberMember = FamilyMember.withId(
            1L, familyId, currentUserId, "일반 구성원", "profile.jpg",
            LocalDateTime.now(), "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            currentUserId, LocalDateTime.now(), currentUserId, LocalDateTime.now()
        );

        // Mocking: 현재 사용자가 Family 구성원인지 확인
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(memberMember));

        // when & then
        assertThatThrownBy(() -> processService.process(command))
            .isInstanceOf(FTException.class)
            .extracting("code")
            .isEqualTo(FamilyExceptionCode.NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("command가 null일 때 NullPointerException이 발생합니다")
    void throw_exception_when_command_is_null() {
        // when & then
        assertThatThrownBy(() -> processService.process(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }
}
