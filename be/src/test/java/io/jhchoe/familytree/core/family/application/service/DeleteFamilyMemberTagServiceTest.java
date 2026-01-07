package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.DeleteFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.out.DeleteFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * DeleteFamilyMemberTagService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] DeleteFamilyMemberTagServiceTest")
class DeleteFamilyMemberTagServiceTest {

    @InjectMocks
    private DeleteFamilyMemberTagService sut;

    @Mock
    private DeleteFamilyMemberTagPort deleteFamilyMemberTagPort;

    @Mock
    private FindFamilyMemberTagPort findFamilyMemberTagPort;

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Nested
    @DisplayName("태그 삭제 테스트")
    class DeleteTagTest {

        @Test
        @DisplayName("태그 삭제가 성공합니다")
        void delete_removes_tag_successfully() {
            // given
            Long familyId = 1L;
            Long tagId = 10L;
            Long currentUserId = 100L;
            DeleteFamilyMemberTagCommand command = new DeleteFamilyMemberTagCommand(familyId, tagId);

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag existingTag = FamilyMemberTag.withId(
                tagId, familyId, "친가", "#D3E5EF", currentUserId, LocalDateTime.now(), null, null
            );

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            when(findFamilyMemberTagPort.findById(tagId)).thenReturn(Optional.of(existingTag));

            // when & then
            assertThatCode(() -> sut.delete(command, currentUserId))
                .doesNotThrowAnyException();

            verify(deleteFamilyMemberTagPort).deleteById(tagId);
        }

        @Test
        @DisplayName("OWNER가 아니면 F010 예외가 발생합니다")
        void delete_throws_F010_when_not_owner() {
            // given
            Long familyId = 1L;
            Long tagId = 10L;
            Long currentUserId = 100L;
            DeleteFamilyMemberTagCommand command = new DeleteFamilyMemberTagCommand(familyId, tagId);

            FamilyMember memberOnly = createMemberOnly(familyId, currentUserId);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(memberOnly));

            // when & then
            assertThatThrownBy(() -> sut.delete(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.NOT_AUTHORIZED.getCode());
                });
        }

        @Test
        @DisplayName("태그가 존재하지 않으면 T005 예외가 발생합니다")
        void delete_throws_T005_when_tag_not_found() {
            // given
            Long familyId = 1L;
            Long tagId = 999L;
            Long currentUserId = 100L;
            DeleteFamilyMemberTagCommand command = new DeleteFamilyMemberTagCommand(familyId, tagId);

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            when(findFamilyMemberTagPort.findById(tagId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.delete(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.TAG_NOT_FOUND.getCode());
                });
        }

        @Test
        @DisplayName("다른 Family의 태그면 T006 예외가 발생합니다")
        void delete_throws_T006_when_tag_belongs_to_other_family() {
            // given
            Long familyId = 1L;
            Long otherFamilyId = 2L;
            Long tagId = 10L;
            Long currentUserId = 100L;
            DeleteFamilyMemberTagCommand command = new DeleteFamilyMemberTagCommand(familyId, tagId);

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag tagFromOtherFamily = FamilyMemberTag.withId(
                tagId, otherFamilyId, "친가", "#D3E5EF", currentUserId, LocalDateTime.now(), null, null
            );

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            when(findFamilyMemberTagPort.findById(tagId)).thenReturn(Optional.of(tagFromOtherFamily));

            // when & then
            assertThatThrownBy(() -> sut.delete(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.TAG_NOT_IN_FAMILY.getCode());
                });
        }
    }

    private FamilyMember createOwnerMember(Long familyId, Long userId) {
        return FamilyMember.withId(
            1L, familyId, userId, "테스트유저", null, null, null, null, null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            userId, LocalDateTime.now(), null, null
        );
    }

    private FamilyMember createMemberOnly(Long familyId, Long userId) {
        return FamilyMember.withId(
            2L, familyId, userId, "일반유저", null, null, null, null, null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            userId, LocalDateTime.now(), null, null
        );
    }
}
