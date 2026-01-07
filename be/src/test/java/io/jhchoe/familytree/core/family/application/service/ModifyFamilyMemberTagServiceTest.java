package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberTagPort;
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
 * ModifyFamilyMemberTagService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] ModifyFamilyMemberTagServiceTest")
class ModifyFamilyMemberTagServiceTest {

    @InjectMocks
    private ModifyFamilyMemberTagService sut;

    @Mock
    private SaveFamilyMemberTagPort saveFamilyMemberTagPort;

    @Mock
    private FindFamilyMemberTagPort findFamilyMemberTagPort;

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Nested
    @DisplayName("태그 수정 테스트")
    class ModifyTagTest {

        @Test
        @DisplayName("이름 수정이 성공합니다")
        void modify_updates_name_successfully() {
            // given
            Long familyId = 1L;
            Long tagId = 10L;
            Long currentUserId = 100L;
            ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
                familyId, tagId, "외가", null
            );

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag existingTag = FamilyMemberTag.withId(
                tagId, familyId, "친가", "#D3E5EF", currentUserId, LocalDateTime.now(), null, null
            );

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            when(findFamilyMemberTagPort.findById(tagId)).thenReturn(Optional.of(existingTag));
            when(findFamilyMemberTagPort.findByFamilyIdAndName(familyId, "외가"))
                .thenReturn(Optional.empty());
            when(saveFamilyMemberTagPort.save(any(FamilyMemberTag.class))).thenReturn(tagId);

            // when & then
            assertThatCode(() -> sut.modify(command, currentUserId))
                .doesNotThrowAnyException();

            verify(saveFamilyMemberTagPort).save(any(FamilyMemberTag.class));
        }

        @Test
        @DisplayName("색상 수정이 성공합니다")
        void modify_updates_color_successfully() {
            // given
            Long familyId = 1L;
            Long tagId = 10L;
            Long currentUserId = 100L;
            ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
                familyId, tagId, "친가", "#FFE2DD"
            );

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag existingTag = FamilyMemberTag.withId(
                tagId, familyId, "친가", "#D3E5EF", currentUserId, LocalDateTime.now(), null, null
            );

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            when(findFamilyMemberTagPort.findById(tagId)).thenReturn(Optional.of(existingTag));
            when(findFamilyMemberTagPort.findByFamilyIdAndName(familyId, "친가"))
                .thenReturn(Optional.of(existingTag)); // 본인은 중복 체크에서 제외
            when(saveFamilyMemberTagPort.save(any(FamilyMemberTag.class))).thenReturn(tagId);

            // when & then
            assertThatCode(() -> sut.modify(command, currentUserId))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("OWNER가 아니면 F010 예외가 발생합니다")
        void modify_throws_F010_when_not_owner() {
            // given
            Long familyId = 1L;
            Long tagId = 10L;
            Long currentUserId = 100L;
            ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
                familyId, tagId, "외가", null
            );

            FamilyMember memberOnly = createMemberOnly(familyId, currentUserId);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(memberOnly));

            // when & then
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.NOT_AUTHORIZED.getCode());
                });
        }

        @Test
        @DisplayName("태그가 존재하지 않으면 T005 예외가 발생합니다")
        void modify_throws_T005_when_tag_not_found() {
            // given
            Long familyId = 1L;
            Long tagId = 999L;
            Long currentUserId = 100L;
            ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
                familyId, tagId, "외가", null
            );

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            when(findFamilyMemberTagPort.findById(tagId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.TAG_NOT_FOUND.getCode());
                });
        }

        @Test
        @DisplayName("다른 Family의 태그면 T006 예외가 발생합니다")
        void modify_throws_T006_when_tag_belongs_to_other_family() {
            // given
            Long familyId = 1L;
            Long otherFamilyId = 2L;
            Long tagId = 10L;
            Long currentUserId = 100L;
            ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
                familyId, tagId, "외가", null
            );

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
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.TAG_NOT_IN_FAMILY.getCode());
                });
        }

        @Test
        @DisplayName("이름이 다른 태그와 중복되면 T002 예외가 발생합니다")
        void modify_throws_T002_when_name_duplicated() {
            // given
            Long familyId = 1L;
            Long tagId = 10L;
            Long currentUserId = 100L;
            ModifyFamilyMemberTagCommand command = new ModifyFamilyMemberTagCommand(
                familyId, tagId, "외가", null
            );

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag existingTag = FamilyMemberTag.withId(
                tagId, familyId, "친가", "#D3E5EF", currentUserId, LocalDateTime.now(), null, null
            );
            FamilyMemberTag duplicateTag = FamilyMemberTag.withId(
                20L, familyId, "외가", "#FFE2DD", currentUserId, LocalDateTime.now(), null, null
            );

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            when(findFamilyMemberTagPort.findById(tagId)).thenReturn(Optional.of(existingTag));
            when(findFamilyMemberTagPort.findByFamilyIdAndName(familyId, "외가"))
                .thenReturn(Optional.of(duplicateTag)); // 다른 태그가 이미 "외가" 사용 중

            // when & then
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.TAG_NAME_DUPLICATED.getCode());
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
