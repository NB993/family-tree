package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.UpdateFamilyMemberStatusCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberStatusHistoryPort;
import io.jhchoe.familytree.core.family.application.port.out.UpdateFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatusHistory;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamilyMemberStatusServiceTest {

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private UpdateFamilyMemberPort updateFamilyMemberPort;

    @Mock
    private SaveFamilyMemberStatusHistoryPort saveFamilyMemberStatusHistoryPort;

    @InjectMocks
    private FamilyMemberStatusService familyMemberStatusService;

    @Test
    @DisplayName("OWNER가 구성원의 상태를 변경할 수 있다")
    void update_status_by_owner_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.INACTIVE;
        String reason = "장기 미접속";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "소유자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // 대상 구성원 (ACTIVE -> INACTIVE로 변경할 대상)
        FamilyMember targetMember = FamilyMember.withRole(
            targetMemberId, familyId, 4L, "구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(targetMember));
        when(updateFamilyMemberPort.update(any(FamilyMember.class)))
            .thenReturn(targetMemberId);
        when(saveFamilyMemberStatusHistoryPort.save(any(FamilyMemberStatusHistory.class)))
            .thenReturn(1L);

        // when
        UpdateFamilyMemberStatusCommand command = new UpdateFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );
        Long updatedId = familyMemberStatusService.updateStatus(command);

        // then
        assertThat(updatedId).isEqualTo(targetMemberId);
        verify(updateFamilyMemberPort, times(1)).update(any(FamilyMember.class));
        verify(saveFamilyMemberStatusHistoryPort, times(1)).save(any(FamilyMemberStatusHistory.class));
    }

    @Test
    @DisplayName("ADMIN이 일반 구성원의 상태를 변경할 수 있다")
    void update_status_by_admin_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.INACTIVE;
        String reason = "장기 미접속";

        // ADMIN 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "관리자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            null, null, null, null
        );

        // 대상 구성원 (일반 구성원)
        FamilyMember targetMember = FamilyMember.withRole(
            targetMemberId, familyId, 4L, "구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(targetMember));
        when(updateFamilyMemberPort.update(any(FamilyMember.class)))
            .thenReturn(targetMemberId);
        when(saveFamilyMemberStatusHistoryPort.save(any(FamilyMemberStatusHistory.class)))
            .thenReturn(1L);

        // when
        UpdateFamilyMemberStatusCommand command = new UpdateFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );
        Long updatedId = familyMemberStatusService.updateStatus(command);

        // then
        assertThat(updatedId).isEqualTo(targetMemberId);
        verify(updateFamilyMemberPort, times(1)).update(any(FamilyMember.class));
        verify(saveFamilyMemberStatusHistoryPort, times(1)).save(any(FamilyMemberStatusHistory.class));
    }

    @Test
    @DisplayName("ADMIN이 다른 ADMIN의 상태를 변경하려고 하면 예외가 발생한다")
    void update_status_of_admin_by_admin_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.INACTIVE;
        String reason = "장기 미접속";

        // ADMIN 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "관리자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            null, null, null, null
        );

        // 대상 구성원 (다른 ADMIN)
        FamilyMember targetMember = FamilyMember.withRole(
            targetMemberId, familyId, 4L, "다른 관리자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(targetMember));

        // when & then
        UpdateFamilyMemberStatusCommand command = new UpdateFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );
        
        assertThatThrownBy(() -> familyMemberStatusService.updateStatus(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCode", FamilyExceptionCode.ADMIN_MODIFICATION_NOT_ALLOWED);
        
        verify(updateFamilyMemberPort, never()).update(any(FamilyMember.class));
        verify(saveFamilyMemberStatusHistoryPort, never()).save(any(FamilyMemberStatusHistory.class));
    }

    @Test
    @DisplayName("일반 구성원이 다른 구성원의 상태를 변경하려고 하면 예외가 발생한다")
    void update_status_by_member_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.INACTIVE;
        String reason = "장기 미접속";

        // 일반 구성원 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "일반 구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // when & then
        UpdateFamilyMemberStatusCommand command = new UpdateFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );
        
        assertThatThrownBy(() -> familyMemberStatusService.updateStatus(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCode", FamilyExceptionCode.NOT_AUTHORIZED);
        
        verify(updateFamilyMemberPort, never()).update(any(FamilyMember.class));
        verify(saveFamilyMemberStatusHistoryPort, never()).save(any(FamilyMemberStatusHistory.class));
    }

    @Test
    @DisplayName("OWNER의 상태를 변경하려고 하면 예외가 발생한다")
    void update_status_of_owner_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.INACTIVE;
        String reason = "장기 미접속";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "소유자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // 대상 구성원 (다른 OWNER)
        FamilyMember targetMember = FamilyMember.withRole(
            targetMemberId, familyId, 4L, "다른 소유자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(targetMember));

        // when & then
        UpdateFamilyMemberStatusCommand command = new UpdateFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );
        
        assertThatThrownBy(() -> familyMemberStatusService.updateStatus(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCode", FamilyExceptionCode.CANNOT_CHANGE_OWNER_STATUS);
        
        verify(updateFamilyMemberPort, never()).update(any(FamilyMember.class));
        verify(saveFamilyMemberStatusHistoryPort, never()).save(any(FamilyMemberStatusHistory.class));
    }
}
