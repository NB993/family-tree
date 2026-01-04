package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberStatusCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberStatusHistoryPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatusHistory;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import io.jhchoe.familytree.test.fixture.FamilyMemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ModifyFamilyMemberStatusService 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] ModifyFamilyMemberStatusServiceTest")
@ExtendWith(MockitoExtension.class)
class ModifyFamilyMemberStatusServiceTest {

    @InjectMocks
    private ModifyFamilyMemberStatusService sut;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private ModifyFamilyMemberPort modifyFamilyMemberPort;

    @Mock
    private SaveFamilyMemberStatusHistoryPort saveFamilyMemberStatusHistoryPort;

    @Test
    @DisplayName("OWNER가 구성원의 상태를 변경할 수 있습니다")
    void modify_status_by_owner_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "장기 미접속";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원 (ACTIVE -> SUSPENDED로 변경할 대상)
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, familyId, 4L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        // Mock 설정
        given(modifyFamilyMemberPort.modify(any(FamilyMember.class))).willReturn(targetMemberId);
        given(saveFamilyMemberStatusHistoryPort.save(any(FamilyMemberStatusHistory.class))).willReturn(1L);

        ModifyFamilyMemberStatusCommand command = new ModifyFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );

        // when
        Long result = sut.modifyStatus(command);

        // then
        assertThat(result).isEqualTo(targetMemberId);
        
        // 상태 변경과 이력 저장이 모두 호출되었는지 검증
        then(modifyFamilyMemberPort).should().modify(argThat(member -> 
            member.getStatus() == newStatus
        ));
        then(saveFamilyMemberStatusHistoryPort).should().save(any(FamilyMemberStatusHistory.class));
    }

    @Test
    @DisplayName("ADMIN이 일반 구성원의 상태를 변경할 수 있습니다")
    void modify_status_by_admin_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "장기 미접속";

        // ADMIN 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.ADMIN);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원 (일반 구성원)
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, familyId, 4L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        // Mock 설정
        given(modifyFamilyMemberPort.modify(any(FamilyMember.class))).willReturn(targetMemberId);
        given(saveFamilyMemberStatusHistoryPort.save(any(FamilyMemberStatusHistory.class))).willReturn(1L);

        ModifyFamilyMemberStatusCommand command = new ModifyFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );

        // when
        Long result = sut.modifyStatus(command);

        // then
        assertThat(result).isEqualTo(targetMemberId);
        then(modifyFamilyMemberPort).should().modify(any(FamilyMember.class));
        then(saveFamilyMemberStatusHistoryPort).should().save(any(FamilyMemberStatusHistory.class));
    }

    @Test
    @DisplayName("현재 사용자가 Family 구성원이 아닌 경우 예외가 발생합니다")
    void throw_exception_when_current_user_is_not_family_member() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "장기 미접속";

        // 현재 사용자가 Family 구성원이 아님
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.empty());

        ModifyFamilyMemberStatusCommand command = new ModifyFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyStatus(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.NOT_FAMILY_MEMBER);
    }

    @Test
    @DisplayName("일반 구성원이 다른 구성원의 상태를 변경하려고 하면 예외가 발생합니다")
    void throw_exception_when_member_tries_to_modify_status() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "장기 미접속";

        // 일반 구성원 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        ModifyFamilyMemberStatusCommand command = new ModifyFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyStatus(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("ADMIN이 다른 ADMIN의 상태를 변경하려고 하면 예외가 발생합니다")
    void throw_exception_when_admin_tries_to_modify_another_admin() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "장기 미접속";

        // ADMIN 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.ADMIN);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원 (다른 ADMIN)
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, familyId, 4L, FamilyMemberRole.ADMIN);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        ModifyFamilyMemberStatusCommand command = new ModifyFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyStatus(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.ADMIN_MODIFICATION_NOT_ALLOWED);
    }

    @Test
    @DisplayName("OWNER의 상태를 변경하려고 하면 예외가 발생합니다")
    void throw_exception_when_trying_to_modify_owner_status() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "장기 미접속";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원 (다른 OWNER)
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, familyId, 4L, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        ModifyFamilyMemberStatusCommand command = new ModifyFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyStatus(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.CANNOT_CHANGE_OWNER_STATUS);
    }

    @Test
    @DisplayName("대상 구성원이 존재하지 않는 경우 예외가 발생합니다")
    void throw_exception_when_target_member_not_found() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberStatus newStatus = FamilyMemberStatus.SUSPENDED;
        String reason = "장기 미접속";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원이 존재하지 않음
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.empty());

        ModifyFamilyMemberStatusCommand command = new ModifyFamilyMemberStatusCommand(
            familyId, targetMemberId, currentUserId, newStatus, reason
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyStatus(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("command가 null인 경우 예외가 발생합니다")
    void throw_exception_when_command_is_null() {
        // given
        ModifyFamilyMemberStatusCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.modifyStatus(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

}
