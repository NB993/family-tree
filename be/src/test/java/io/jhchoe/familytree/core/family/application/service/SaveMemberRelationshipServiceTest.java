package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberRelationshipCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberRelationshipPort;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationship;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRelationshipType;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
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
    private FamilyMemberJpaRepository familyMemberRepository;

    @InjectMocks
    private SaveFamilyMemberRelationshipService sut;

    @Test
    @DisplayName("saveRelationship 메서드는 기존 관계가 없을 때 새 관계를 생성해야 한다")
    void given_no_existing_relationship_when_save_relationship_then_create_new_relationship() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        Long userId = 100L;
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
        
        when(familyMemberRepository.existsByFamilyIdAndUserId(familyId, userId))
            .thenReturn(true);
        
        when(findFamilyMemberRelationshipPort.findRelationship(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.empty());
        
        when(saveFamilyMemberRelationshipPort.saveRelationship(any(FamilyMemberRelationship.class)))
            .thenReturn(expectedId);

        // when
        Long result = sut.saveRelationship(command);

        // then
        assertThat(result).isEqualTo(expectedId);
        verify(familyMemberRepository).existsByFamilyIdAndUserId(familyId, userId);
        verify(findFamilyMemberRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
        verify(saveFamilyMemberRelationshipPort).saveRelationship(any(FamilyMemberRelationship.class));
    }

    @Test
    @DisplayName("saveRelationship 메서드는 기존 관계가 있을 때 관계를 업데이트해야 한다")
    void given_existing_relationship_when_save_relationship_then_update_relationship() {
        // given
        Long relationshipId = 1L;
        Long familyId = 2L;
        Long fromMemberId = 3L;
        Long toMemberId = 4L;
        Long userId = 100L;
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
        
        when(familyMemberRepository.existsByFamilyIdAndUserId(familyId, userId))
            .thenReturn(true);
        
        when(findFamilyMemberRelationshipPort.findRelationship(familyId, fromMemberId, toMemberId))
            .thenReturn(Optional.of(existingRelationship));
        
        when(saveFamilyMemberRelationshipPort.saveRelationship(any(FamilyMemberRelationship.class)))
            .thenReturn(relationshipId);

        // when
        Long result = sut.saveRelationship(command);

        // then
        assertThat(result).isEqualTo(relationshipId);
        verify(familyMemberRepository).existsByFamilyIdAndUserId(familyId, userId);
        verify(findFamilyMemberRelationshipPort).findRelationship(familyId, fromMemberId, toMemberId);
        verify(saveFamilyMemberRelationshipPort).saveRelationship(any(FamilyMemberRelationship.class));
    }
    
    @Test
    @DisplayName("saveRelationship 메서드는 사용자가 가족 구성원이 아닐 경우 FTException을 발생시켜야 한다")
    void given_non_family_member_when_save_relationship_then_throw_ft_exception() {
        // given
        Long familyId = 1L;
        Long fromMemberId = 2L;
        Long toMemberId = 3L;
        Long userId = 100L;
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
        
        when(familyMemberRepository.existsByFamilyIdAndUserId(familyId, userId))
            .thenReturn(false);
        
        // when & then
        assertThatThrownBy(() -> sut.saveRelationship(command))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.NOT_FAMILY_MEMBER);
        
        verify(familyMemberRepository).existsByFamilyIdAndUserId(familyId, userId);
        verifyNoInteractions(findFamilyMemberRelationshipPort);
        verifyNoInteractions(saveFamilyMemberRelationshipPort);
    }

    @Test
    @DisplayName("saveRelationship 메서드는 command가 null이면 예외를 발생시켜야 한다")
    void given_null_command_when_save_relationship_then_throw_exception() {
        // given
        SaveFamilyMemberRelationshipCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.saveRelationship(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
        
        verifyNoInteractions(familyMemberRepository);
        verifyNoInteractions(findFamilyMemberRelationshipPort);
        verifyNoInteractions(saveFamilyMemberRelationshipPort);
    }
    
    @Test
    @DisplayName("saveRelationship 메서드는 userId가 null이면 예외를 발생시켜야 한다")
    void given_null_user_id_when_save_relationship_then_throw_exception() {
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

        // when & then
        assertThatThrownBy(() -> sut.saveRelationship(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
        
        verifyNoInteractions(familyMemberRepository);
        verifyNoInteractions(findFamilyMemberRelationshipPort);
        verifyNoInteractions(saveFamilyMemberRelationshipPort);
    }
}
