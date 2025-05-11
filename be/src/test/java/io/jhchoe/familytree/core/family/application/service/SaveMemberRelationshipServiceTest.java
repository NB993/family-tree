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
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyValidationService;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] FamilyRelationshipService")
@ExtendWith(MockitoExtension.class)
class SaveMemberRelationshipServiceTest {

    @Mock
    private SaveFamilyMemberRelationshipPort saveFamilyMemberRelationshipPort;

    @Mock
    private FindFamilyMemberRelationshipPort findFamilyMemberRelationshipPort;

    @Mock
    private FamilyValidationService familyValidationService;

    @InjectMocks
    private SaveFamilyMemberRelationshipService sut;

    @Test
    @DisplayName("save 메서드는 command가 null이면 예외를 발생시켜야 한다")
    void given_null_command_when_save_then_throw_exception() {
        // given
        SaveFamilyMemberRelationshipCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");

        verifyNoInteractions(familyValidationService);
        verifyNoInteractions(findFamilyMemberRelationshipPort);
        verifyNoInteractions(saveFamilyMemberRelationshipPort);
    }

    @Test
    @DisplayName("save 메서드는 기존 관계가 없을 때 새 관계를 생성해야 한다")
    void given_no_existing_relationship_when_save_relationship_then_create_new_relationship() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "새로운 관계";
        Long expectedId = 4L;

        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        doNothing().when(familyValidationService).validateFamilyExists(familyId);
        doNothing().when(familyValidationService).validateFamilyAccess(familyId, fromMemberId);

        when(findFamilyMemberRelationshipPort.findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.empty());

        when(saveFamilyMemberRelationshipPort.save(any(FamilyMemberRelationship.class)))
            .thenReturn(expectedId);

        // when
        Long result = sut.save(command);

        // then
        assertThat(result).isEqualTo(expectedId);
        verify(familyValidationService).validateFamilyExists(familyId);
        verify(familyValidationService).validateFamilyAccess(familyId, fromMemberId);
        verify(findFamilyMemberRelationshipPort).findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId);
        verify(saveFamilyMemberRelationshipPort).save(any(FamilyMemberRelationship.class));
    }

    @Test
    @DisplayName("save 메서드는 기존 관계가 있을 때 관계를 업데이트해야 한다")
    void given_existing_relationship_when_save_then_update_relationship() {
        // given
        Long relationshipId = 1L;
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        FamilyMemberRelationshipType originalType = FamilyMemberRelationshipType.PARENT;
        FamilyMemberRelationshipType newType = FamilyMemberRelationshipType.GRANDPARENT;
        String originalCustom = null;
        String newCustom = null;
        String originalDesc = "원래 설명";
        String newDesc = "새로운 설명";

        FamilyMemberRelationship existingRelationship = FamilyMemberRelationship.withId(
            relationshipId,
            familyId,
            fromMemberId,
            toMemberId,
            originalType,
            originalCustom,
            originalDesc,
            null,
            null,
            null,
            null
        );

        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            newType,
            newCustom,
            newDesc
        );

        doNothing().when(familyValidationService).validateFamilyExists(familyId);
        doNothing().when(familyValidationService).validateFamilyAccess(familyId, fromMemberId);

        when(findFamilyMemberRelationshipPort.findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.of(existingRelationship));

        when(saveFamilyMemberRelationshipPort.save(any(FamilyMemberRelationship.class)))
            .thenReturn(relationshipId);

        // when
        Long result = sut.save(command);

        // then
        assertThat(result).isEqualTo(relationshipId);
        verify(familyValidationService).validateFamilyExists(familyId);
        verify(familyValidationService).validateFamilyAccess(familyId, fromMemberId);
        verify(findFamilyMemberRelationshipPort).findByFamilyIdAndFromMemberIdAndToMemberId(familyId, fromMemberId, toMemberId);
        verify(saveFamilyMemberRelationshipPort).save(any(FamilyMemberRelationship.class));
    }

    @Test
    @DisplayName("save 메서드는 familyId와 일치하는 Family가 존재하지 않는 경우 예외를 발생시켜야 한다")
    void given_non_family_when_save_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "새로운 관계";

        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        doThrow(FTException.class)
            .when(familyValidationService).validateFamilyExists(familyId);

        // when & then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class);

        verify(familyValidationService).validateFamilyExists(familyId);
        verify(familyValidationService, never()).validateFamilyAccess(anyLong(), anyLong());
        verifyNoInteractions(findFamilyMemberRelationshipPort);
        verifyNoInteractions(saveFamilyMemberRelationshipPort);
    }

    @Test
    @DisplayName("save 메서드는 familyId, fromMemberId와 일치하는 FamilyMember가 존재하지 않는 경우 예외를 발생시켜야 한다")
    void given_non_family_member_when_save_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        FamilyMemberRelationshipType relationshipType = FamilyMemberRelationshipType.PARENT;
        String customRelationship = null;
        String description = "새로운 관계";

        SaveFamilyMemberRelationshipCommand command = new SaveFamilyMemberRelationshipCommand(
            familyId,
            fromMemberId,
            toMemberId,
            relationshipType,
            customRelationship,
            description
        );

        doThrow(FTException.class)
            .when(familyValidationService).validateFamilyAccess(familyId, fromMemberId);

        // when & then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(FTException.class);

        verify(familyValidationService).validateFamilyExists(familyId);
        verify(familyValidationService).validateFamilyAccess(familyId, fromMemberId);
        verifyNoInteractions(findFamilyMemberRelationshipPort);
        verifyNoInteractions(saveFamilyMemberRelationshipPort);
    }
}
