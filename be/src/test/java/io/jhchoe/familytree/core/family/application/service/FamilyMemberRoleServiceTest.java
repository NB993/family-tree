package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersRoleQuery;
import io.jhchoe.familytree.core.family.application.port.in.UpdateFamilyMemberRoleCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.UpdateFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamilyMemberRoleServiceTest {

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private UpdateFamilyMemberPort updateFamilyMemberPort;

    @InjectMocks
    private FamilyMemberRoleService familyMemberRoleService;

    @Test
    @DisplayName("OWNER가 구성원의 역할을 ADMIN으로 변경할 수 있다")
    void update_role_to_admin_by_owner_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "소유자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // 대상 구성원 (MEMBER -> ADMIN으로 변경할 대상)
        FamilyMember targetMember = FamilyMember.withRole(
            targetMemberId, familyId, 4L, "구성원", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );

        // 역할이 변경된 구성원
        FamilyMember updatedMember = targetMember.updateRole(FamilyMemberRole.ADMIN);

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(targetMember));
        when(updateFamilyMemberPort.update(any(FamilyMember.class)))
            .thenReturn(targetMemberId);

        // when
        UpdateFamilyMemberRoleCommand command = new UpdateFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, newRole
        );
        Long updatedId = familyMemberRoleService.updateRole(command);

        // then
        assertThat(updatedId).isEqualTo(targetMemberId);
        verify(updateFamilyMemberPort, times(1)).update(any(FamilyMember.class));
    }

    @Test
    @DisplayName("OWNER가 아닌 사용자가 구성원의 역할 변경을 시도하면 예외가 발생한다")
    void update_role_by_non_owner_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // ADMIN 권한을 가진 현재 사용자 (OWNER가 아님)
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "관리자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // when & then
        UpdateFamilyMemberRoleCommand command = new UpdateFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, newRole
        );
        
        assertThatThrownBy(() -> familyMemberRoleService.updateRole(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCode", FamilyExceptionCode.NOT_AUTHORIZED);
        
        verify(updateFamilyMemberPort, never()).update(any(FamilyMember.class));
    }

    @Test
    @DisplayName("Family 구성원이 아닌 사용자가 역할 변경을 시도하면 예외가 발생한다")
    void update_role_by_non_member_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // 구성원이 아닌 경우를 시뮬레이션
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.empty());

        // when & then
        UpdateFamilyMemberRoleCommand command = new UpdateFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, newRole
        );
        
        assertThatThrownBy(() -> familyMemberRoleService.updateRole(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCode", FamilyExceptionCode.NOT_FAMILY_MEMBER);
        
        verify(updateFamilyMemberPort, never()).update(any(FamilyMember.class));
    }

    @Test
    @DisplayName("존재하지 않는 구성원의 역할 변경을 시도하면 예외가 발생한다")
    void update_role_of_non_existing_member_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "소유자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // 존재하지 않는 구성원
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.empty());

        // when & then
        UpdateFamilyMemberRoleCommand command = new UpdateFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, newRole
        );
        
        assertThatThrownBy(() -> familyMemberRoleService.updateRole(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCode", FamilyExceptionCode.MEMBER_NOT_FOUND);
        
        verify(updateFamilyMemberPort, never()).update(any(FamilyMember.class));
    }

    @Test
    @DisplayName("OWNER 역할의 구성원은 역할을 변경할 수 없다")
    void update_role_of_owner_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRole newRole = FamilyMemberRole.ADMIN;

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            3L, familyId, currentUserId, "소유자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // 변경하려는 대상도 OWNER
        FamilyMember targetMember = FamilyMember.withRole(
            targetMemberId, familyId, 4L, "소유자2", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(targetMember));

        // when & then
        UpdateFamilyMemberRoleCommand command = new UpdateFamilyMemberRoleCommand(
            familyId, targetMemberId, currentUserId, newRole
        );
        
        assertThatThrownBy(() -> familyMemberRoleService.updateRole(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCode", FamilyExceptionCode.CANNOT_CHANGE_OWNER_ROLE);
        
        verify(updateFamilyMemberPort, never()).update(any(FamilyMember.class));
    }

    @Test
    @DisplayName("구성원 역할 목록 조회에 성공한다")
    void find_all_members_role_should_succeed() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;

        // 현재 사용자
        FamilyMember currentMember = FamilyMember.withRole(
            2L, familyId, currentUserId, "사용자", "profile.jpg", LocalDateTime.now(),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );

        // 전체 구성원 목록
        List<FamilyMember> allMembers = Arrays.asList(
            FamilyMember.withRole(
                1L, familyId, 3L, "소유자", "profile1.jpg", LocalDateTime.now(),
                "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
                null, null, null, null
            ),
            currentMember,
            FamilyMember.withRole(
                3L, familyId, 4L, "관리자", "profile2.jpg", LocalDateTime.now(),
                "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
                null, null, null, null
            )
        );

        // Mock 설정
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));
        when(findFamilyMemberPort.findAllByFamilyId(familyId))
            .thenReturn(allMembers);

        // when
        FindFamilyMembersRoleQuery query = new FindFamilyMembersRoleQuery(familyId, currentUserId);
        List<FamilyMember> result = familyMemberRoleService.findAllByFamilyId(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getRole()).isEqualTo(FamilyMemberRole.OWNER);
        assertThat(result.get(1).getRole()).isEqualTo(FamilyMemberRole.MEMBER);
        assertThat(result.get(2).getRole()).isEqualTo(FamilyMemberRole.ADMIN);
    }

    @Test
    @DisplayName("Family 구성원이 아닌 사용자가 역할 목록 조회를 시도하면 예외가 발생한다")
    void find_all_by_non_member_should_throw_exception() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;

        // 구성원이 아닌 경우를 시뮬레이션
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.empty());

        // when & then
        FindFamilyMembersRoleQuery query = new FindFamilyMembersRoleQuery(familyId, currentUserId);
        
        assertThatThrownBy(() -> familyMemberRoleService.findAllByFamilyId(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCode", FamilyExceptionCode.NOT_FAMILY_MEMBER);
    }
}
