package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberInfoCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import io.jhchoe.familytree.test.fixture.FamilyMemberFixture;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ModifyFamilyMemberInfoService 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] ModifyFamilyMemberInfoServiceTest")
@ExtendWith(MockitoExtension.class)
class ModifyFamilyMemberInfoServiceTest {

    @InjectMocks
    private ModifyFamilyMemberInfoService sut;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private ModifyFamilyMemberPort modifyFamilyMemberPort;

    @Test
    @DisplayName("OWNER가 구성원의 기본 정보를 변경할 수 있습니다")
    void modify_info_by_owner_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        String newName = "김철수";
        LocalDateTime newBirthday = LocalDateTime.of(1985, 3, 15, 0, 0);
        BirthdayType newBirthdayType = BirthdayType.LUNAR;

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, familyId, 4L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        // Mock 설정
        given(modifyFamilyMemberPort.modify(any(FamilyMember.class))).willReturn(targetMemberId);

        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, targetMemberId, currentUserId, newName, newBirthday, newBirthdayType
        );

        // when
        Long result = sut.modifyInfo(command);

        // then
        assertThat(result).isEqualTo(targetMemberId);
        then(modifyFamilyMemberPort).should().modify(argThat(member ->
            member.getName().equals(newName) &&
            member.getBirthday().equals(newBirthday) &&
            member.getBirthdayType() == newBirthdayType
        ));
    }

    @Test
    @DisplayName("ADMIN이 일반 구성원의 기본 정보를 변경할 수 있습니다")
    void modify_info_by_admin_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        String newName = "김철수";
        LocalDateTime newBirthday = LocalDateTime.of(1985, 3, 15, 0, 0);
        BirthdayType newBirthdayType = BirthdayType.SOLAR;

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

        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, targetMemberId, currentUserId, newName, newBirthday, newBirthdayType
        );

        // when
        Long result = sut.modifyInfo(command);

        // then
        assertThat(result).isEqualTo(targetMemberId);
        then(modifyFamilyMemberPort).should().modify(any(FamilyMember.class));
    }

    @Test
    @DisplayName("현재 사용자가 Family 구성원이 아닌 경우 예외가 발생합니다")
    void throw_exception_when_current_user_is_not_family_member() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        String newName = "김철수";

        // 현재 사용자가 Family 구성원이 아님
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.empty());

        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, targetMemberId, currentUserId, newName, null, null
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyInfo(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.NOT_FAMILY_MEMBER);
    }

    @Test
    @DisplayName("일반 구성원이 다른 구성원의 정보를 변경하려고 하면 예외가 발생합니다")
    void throw_exception_when_member_tries_to_modify_info() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        String newName = "김철수";

        // 일반 구성원 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, targetMemberId, currentUserId, newName, null, null
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyInfo(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("대상 구성원이 존재하지 않는 경우 예외가 발생합니다")
    void throw_exception_when_target_member_not_found() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        String newName = "김철수";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원이 존재하지 않음
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.empty());

        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, targetMemberId, currentUserId, newName, null, null
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyInfo(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("대상 구성원이 다른 Family에 속해있는 경우 예외가 발생합니다")
    void throw_exception_when_target_member_belongs_to_different_family() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        String newName = "김철수";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원 (다른 Family에 속함)
        Long otherFamilyId = 999L;
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, otherFamilyId, 4L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, targetMemberId, currentUserId, newName, null, null
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyInfo(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("본인의 정보를 수정하려고 하면 예외가 발생합니다")
    void throw_exception_when_trying_to_modify_self() {
        // given
        Long familyId = 1L;
        Long currentUserId = 3L;
        String newName = "김철수";

        // OWNER 권한을 가진 현재 사용자 (본인)
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(10L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원도 본인 (같은 userId)
        given(findFamilyMemberPort.findById(10L))
            .willReturn(Optional.of(currentMember));

        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, 10L, currentUserId, newName, null, null
        );

        // when & then
        assertThatThrownBy(() -> sut.modifyInfo(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.SELF_MODIFICATION_NOT_ALLOWED);
    }

    @Test
    @DisplayName("command가 null인 경우 예외가 발생합니다")
    void throw_exception_when_command_is_null() {
        // given
        ModifyFamilyMemberInfoCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.modifyInfo(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("생일과 생일타입이 null이어도 정상 동작합니다")
    void modify_info_with_null_birthday_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        String newName = "김철수";

        // OWNER 권한을 가진 현재 사용자
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.OWNER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, familyId, 4L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        // Mock 설정
        given(modifyFamilyMemberPort.modify(any(FamilyMember.class))).willReturn(targetMemberId);

        ModifyFamilyMemberInfoCommand command = new ModifyFamilyMemberInfoCommand(
            familyId, targetMemberId, currentUserId, newName, null, null
        );

        // when
        Long result = sut.modifyInfo(command);

        // then
        assertThat(result).isEqualTo(targetMemberId);
        then(modifyFamilyMemberPort).should().modify(argThat(member ->
            member.getName().equals(newName) &&
            member.getBirthday() == null &&
            member.getBirthdayType() == null
        ));
    }
}
