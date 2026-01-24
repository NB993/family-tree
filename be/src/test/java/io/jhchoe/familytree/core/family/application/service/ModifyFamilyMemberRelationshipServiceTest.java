package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
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
 * ModifyFamilyMemberRelationshipService 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] ModifyFamilyMemberRelationshipServiceTest")
@ExtendWith(MockitoExtension.class)
class ModifyFamilyMemberRelationshipServiceTest {

    @InjectMocks
    private ModifyFamilyMemberRelationshipService sut;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private ModifyFamilyMemberPort modifyFamilyMemberPort;

    @Test
    @DisplayName("구성원이 관계를 변경할 수 있습니다")
    void modify_relationship_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRelationshipType newRelationshipType = FamilyMemberRelationshipType.FATHER;

        // 현재 사용자 (Family 구성원)
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, familyId, 4L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        // Mock 설정
        given(modifyFamilyMemberPort.modify(any(FamilyMember.class))).willReturn(targetMemberId);

        ModifyFamilyMemberRelationshipCommand command = new ModifyFamilyMemberRelationshipCommand(
            familyId, targetMemberId, currentUserId, newRelationshipType, null
        );

        // when
        Long result = sut.modify(command);

        // then
        assertThat(result).isEqualTo(targetMemberId);

        then(modifyFamilyMemberPort).should().modify(argThat(member ->
            member.getRelationshipType() == newRelationshipType
        ));
    }

    @Test
    @DisplayName("CUSTOM 타입으로 관계를 변경할 수 있습니다")
    void modify_relationship_with_custom_type_should_succeed() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRelationshipType newRelationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = "의붓아버지";

        // 현재 사용자 (Family 구성원)
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, familyId, 4L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        // Mock 설정
        given(modifyFamilyMemberPort.modify(any(FamilyMember.class))).willReturn(targetMemberId);

        ModifyFamilyMemberRelationshipCommand command = new ModifyFamilyMemberRelationshipCommand(
            familyId, targetMemberId, currentUserId, newRelationshipType, customRelationship
        );

        // when
        Long result = sut.modify(command);

        // then
        assertThat(result).isEqualTo(targetMemberId);

        then(modifyFamilyMemberPort).should().modify(argThat(member ->
            member.getRelationshipType() == newRelationshipType &&
            customRelationship.equals(member.getCustomRelationship())
        ));
    }

    @Test
    @DisplayName("현재 사용자가 Family 구성원이 아닌 경우 예외가 발생합니다")
    void throw_exception_when_current_user_is_not_family_member() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRelationshipType newRelationshipType = FamilyMemberRelationshipType.FATHER;

        // 현재 사용자가 Family 구성원이 아님
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.empty());

        ModifyFamilyMemberRelationshipCommand command = new ModifyFamilyMemberRelationshipCommand(
            familyId, targetMemberId, currentUserId, newRelationshipType, null
        );

        // when & then
        assertThatThrownBy(() -> sut.modify(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.NOT_FAMILY_MEMBER);
    }

    @Test
    @DisplayName("대상 구성원이 존재하지 않는 경우 예외가 발생합니다")
    void throw_exception_when_target_member_not_found() {
        // given
        Long familyId = 1L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRelationshipType newRelationshipType = FamilyMemberRelationshipType.FATHER;

        // 현재 사용자 (Family 구성원)
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원이 존재하지 않음
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.empty());

        ModifyFamilyMemberRelationshipCommand command = new ModifyFamilyMemberRelationshipCommand(
            familyId, targetMemberId, currentUserId, newRelationshipType, null
        );

        // when & then
        assertThatThrownBy(() -> sut.modify(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("대상 구성원이 다른 Family 소속인 경우 예외가 발생합니다")
    void throw_exception_when_target_member_belongs_to_different_family() {
        // given
        Long familyId = 1L;
        Long differentFamilyId = 99L;
        Long targetMemberId = 2L;
        Long currentUserId = 3L;
        FamilyMemberRelationshipType newRelationshipType = FamilyMemberRelationshipType.FATHER;

        // 현재 사용자 (Family 구성원)
        FamilyMember currentMember = FamilyMemberFixture.withIdAndRole(3L, familyId, currentUserId, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));

        // 대상 구성원 (다른 Family 소속)
        FamilyMember targetMember = FamilyMemberFixture.withIdAndRole(targetMemberId, differentFamilyId, 4L, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findById(targetMemberId))
            .willReturn(Optional.of(targetMember));

        ModifyFamilyMemberRelationshipCommand command = new ModifyFamilyMemberRelationshipCommand(
            familyId, targetMemberId, currentUserId, newRelationshipType, null
        );

        // when & then
        assertThatThrownBy(() -> sut.modify(command))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("command가 null인 경우 예외가 발생합니다")
    void throw_exception_when_command_is_null() {
        // given
        ModifyFamilyMemberRelationshipCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.modify(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }
}
