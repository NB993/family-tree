package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRoleCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ModifyFamilyMemberRoleService 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] ModifyFamilyMemberRoleServiceTest")
@ExtendWith(MockitoExtension.class)
class ModifyFamilyMemberRoleServiceTest {

    @InjectMocks
    private ModifyFamilyMemberRoleService sut;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private ModifyFamilyMemberPort modifyFamilyMemberPort;

    @Test
    @DisplayName("OWNER가 다른 구성원의 역할을 ADMIN으로 변경할 수 있습니다")
    void modify_member_role_to_admin_by_owner() {
        // given
        Long familyId = 1L;
        Long currentUserId = 10L;
        Long targetMemberId = 20L;
        
        // 현재 사용자는 OWNER
        FamilyMember currentMember = createFamilyMember(15L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));
        
        // 대상 구성원은 MEMBER
        FamilyMember targetMember = createFamilyMember(targetMemberId, familyId, 30L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));
        
        // modify 포트 호출 시 성공적으로 업데이트된 ID 반환
        given(modifyFamilyMemberPort.modify(any(FamilyMember.class))).willReturn(targetMemberId);
        
        ModifyFamilyMemberRoleCommand command = new ModifyFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, FamilyMemberRole.ADMIN
        );

        // when
        Long result = sut.modifyRole(command);

        // then
        assertThat(result).isEqualTo(targetMemberId);
        
        // modify 포트가 올바른 역할로 호출되었는지 검증
        then(modifyFamilyMemberPort).should().modify(argThat(member -> 
            member.getRole() == FamilyMemberRole.ADMIN
        ));
    }

    @Test
    @DisplayName("현재 사용자가 Family 구성원이 아닌 경우 예외가 발생합니다")
    void throw_exception_when_current_user_is_not_family_member() {
        // given
        Long familyId = 1L;
        Long currentUserId = 10L;
        Long targetMemberId = 20L;
        
        // 현재 사용자가 Family 구성원이 아님
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.empty());
        
        ModifyFamilyMemberRoleCommand command = new ModifyFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, FamilyMemberRole.ADMIN
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyRole(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.NOT_FAMILY_MEMBER);
    }

    @Test
    @DisplayName("OWNER가 아닌 사용자가 역할 변경을 시도하면 예외가 발생합니다")
    void throw_exception_when_non_owner_tries_to_modify_role() {
        // given
        Long familyId = 1L;
        Long currentUserId = 10L;
        Long targetMemberId = 20L;
        
        // 현재 사용자는 ADMIN (OWNER가 아님)
        FamilyMember currentMember = createFamilyMember(15L, familyId, currentUserId, FamilyMemberRole.ADMIN);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));
        
        ModifyFamilyMemberRoleCommand command = new ModifyFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, FamilyMemberRole.ADMIN
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyRole(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("대상 구성원이 존재하지 않는 경우 예외가 발생합니다")
    void throw_exception_when_target_member_not_found() {
        // given
        Long familyId = 1L;
        Long currentUserId = 10L;
        Long targetMemberId = 20L;
        
        // 현재 사용자는 OWNER
        FamilyMember currentMember = createFamilyMember(15L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));
        
        // 대상 구성원이 존재하지 않음
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.empty());
        
        ModifyFamilyMemberRoleCommand command = new ModifyFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, FamilyMemberRole.ADMIN
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyRole(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("대상 구성원이 다른 Family에 속해있는 경우 예외가 발생합니다")
    void throw_exception_when_target_member_belongs_to_different_family() {
        // given
        Long familyId = 1L;
        Long differentFamilyId = 999L;
        Long currentUserId = 10L;
        Long targetMemberId = 20L;
        
        // 현재 사용자는 OWNER
        FamilyMember currentMember = createFamilyMember(15L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));
        
        // 대상 구성원은 다른 Family에 속함
        FamilyMember targetMember = createFamilyMember(targetMemberId, differentFamilyId, 30L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));
        
        ModifyFamilyMemberRoleCommand command = new ModifyFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, FamilyMemberRole.ADMIN
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyRole(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("OWNER의 역할을 변경하려고 하면 예외가 발생합니다")
    void throw_exception_when_trying_to_modify_owner_role() {
        // given
        Long familyId = 1L;
        Long currentUserId = 10L;
        Long targetMemberId = 20L;
        
        // 현재 사용자는 OWNER
        FamilyMember currentMember = createFamilyMember(15L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));
        
        // 대상 구성원도 OWNER (다른 OWNER의 역할 변경 시도)
        FamilyMember targetMember = createFamilyMember(targetMemberId, familyId, 30L, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));
        
        ModifyFamilyMemberRoleCommand command = new ModifyFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, FamilyMemberRole.ADMIN
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyRole(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.CANNOT_CHANGE_OWNER_ROLE);
    }

    @Test
    @DisplayName("command가 null인 경우 예외가 발생합니다")
    void throw_exception_when_command_is_null() {
        // given
        ModifyFamilyMemberRoleCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.modifyRole(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    /**
     * 테스트용 FamilyMember 객체를 생성합니다.
     */
    private FamilyMember createFamilyMember(Long id, Long familyId, Long userId, FamilyMemberRole role) {
        return FamilyMember.withId(
            id, familyId, userId, null, "Test User", null, "profile.jpg",
            LocalDateTime.now(), "KR", FamilyMemberStatus.ACTIVE, role,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
    }
}
