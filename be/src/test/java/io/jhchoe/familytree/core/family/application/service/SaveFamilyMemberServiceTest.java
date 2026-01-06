package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberCommand;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyValidationService;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] SaveFamilyMemberServiceTest")
@ExtendWith(MockitoExtension.class)
class SaveFamilyMemberServiceTest {

    @Mock
    private SaveFamilyMemberPort saveFamilyMemberPort;

    @Mock
    private FamilyValidationService familyValidationService;

    @InjectMocks
    private SaveFamilyMemberService sut;

    @Test
    @DisplayName("command가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_command_is_null() {
        // given
        SaveFamilyMemberCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.save(command, 1L))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("command");

        verifyNoInteractions(familyValidationService);
        verifyNoInteractions(saveFamilyMemberPort);
    }

    @Test
    @DisplayName("currentUserId가 null인 경우 NullPointerException이 발생합니다")
    void throw_exception_when_current_user_id_is_null() {
        // given
        SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
            1L,
            "홍길동",
            null,
            null,
            null
        );

        // when & then
        assertThatThrownBy(() -> sut.save(command, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("currentUserId");

        verifyNoInteractions(familyValidationService);
        verifyNoInteractions(saveFamilyMemberPort);
    }

    @Test
    @DisplayName("유효한 command로 구성원을 정상 저장합니다")
    void save_member_successfully_when_valid_command() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        String name = "홍길동";
        LocalDateTime birthday = LocalDateTime.of(1990, 1, 15, 0, 0);
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.FATHER;
        Long expectedMemberId = 100L;

        SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
            familyId,
            name,
            birthday,
            relationshipType,
            null
        );

        // Mocking: Family 존재 확인
        doNothing().when(familyValidationService).validateFamilyExists(familyId);
        // Mocking: 현재 사용자가 Family 구성원인지 확인
        doNothing().when(familyValidationService).validateFamilyAccess(familyId, currentUserId);
        // Mocking: 저장 성공
        when(saveFamilyMemberPort.save(any(FamilyMember.class))).thenReturn(expectedMemberId);

        // when
        Long result = sut.save(command, currentUserId);

        // then
        assertThat(result).isEqualTo(expectedMemberId);
        verify(familyValidationService).validateFamilyExists(familyId);
        verify(familyValidationService).validateFamilyAccess(familyId, currentUserId);
        verify(saveFamilyMemberPort).save(any(FamilyMember.class));
    }

    @Test
    @DisplayName("CUSTOM 관계 타입과 사용자 정의 관계명으로 구성원을 저장합니다")
    void save_member_with_custom_relationship() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        String name = "홍길동";
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.CUSTOM;
        String customRelationship = "외할아버지";
        Long expectedMemberId = 100L;

        SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
            familyId,
            name,
            null,
            relationshipType,
            customRelationship
        );

        // Mocking: Family 존재 확인
        doNothing().when(familyValidationService).validateFamilyExists(familyId);
        // Mocking: 현재 사용자가 Family 구성원인지 확인
        doNothing().when(familyValidationService).validateFamilyAccess(familyId, currentUserId);
        // Mocking: 저장 성공
        when(saveFamilyMemberPort.save(any(FamilyMember.class))).thenReturn(expectedMemberId);

        // when
        Long result = sut.save(command, currentUserId);

        // then
        assertThat(result).isEqualTo(expectedMemberId);
        verify(saveFamilyMemberPort).save(any(FamilyMember.class));
    }

    @Test
    @DisplayName("Family가 존재하지 않는 경우 FTException이 발생합니다")
    void throw_exception_when_family_not_found() {
        // given
        Long familyId = 999L;
        Long currentUserId = 1L;
        SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
            familyId,
            "홍길동",
            null,
            null,
            null
        );

        // Mocking: Family 존재하지 않음
        doThrow(FTException.class)
            .when(familyValidationService).validateFamilyExists(familyId);

        // when & then
        assertThatThrownBy(() -> sut.save(command, currentUserId))
            .isInstanceOf(FTException.class);

        verify(familyValidationService).validateFamilyExists(familyId);
        verify(familyValidationService, never()).validateFamilyAccess(anyLong(), anyLong());
        verifyNoInteractions(saveFamilyMemberPort);
    }

    @Test
    @DisplayName("현재 사용자가 Family 구성원이 아닌 경우 FTException이 발생합니다")
    void throw_exception_when_not_family_member() {
        // given
        Long familyId = 1L;
        Long currentUserId = 999L;
        SaveFamilyMemberCommand command = new SaveFamilyMemberCommand(
            familyId,
            "홍길동",
            null,
            null,
            null
        );

        // Mocking: Family 존재
        doNothing().when(familyValidationService).validateFamilyExists(familyId);
        // Mocking: 현재 사용자가 Family 구성원이 아님
        doThrow(FTException.class)
            .when(familyValidationService).validateFamilyAccess(familyId, currentUserId);

        // when & then
        assertThatThrownBy(() -> sut.save(command, currentUserId))
            .isInstanceOf(FTException.class);

        verify(familyValidationService).validateFamilyExists(familyId);
        verify(familyValidationService).validateFamilyAccess(familyId, currentUserId);
        verifyNoInteractions(saveFamilyMemberPort);
    }
}
