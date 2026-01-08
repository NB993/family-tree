package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagMappingInfo;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyMemberTagMappingCommand;
import io.jhchoe.familytree.core.family.application.port.out.DeleteFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ModifyFamilyMemberTagMappingService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] ModifyFamilyMemberTagMappingServiceTest")
class ModifyFamilyMemberTagMappingServiceTest {

    @InjectMocks
    private ModifyFamilyMemberTagMappingService sut;

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Mock
    private FindFamilyMemberTagPort findFamilyMemberTagPort;

    @Mock
    private SaveFamilyMemberTagMappingPort saveFamilyMemberTagMappingPort;

    @Mock
    private DeleteFamilyMemberTagMappingPort deleteFamilyMemberTagMappingPort;

    @Nested
    @DisplayName("태그 할당 성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("멤버에 태그를 할당합니다")
        void modify_assigns_tags_to_member() {
            // given
            Long familyId = 1L;
            Long memberId = 10L;
            Long currentUserId = 100L;
            List<Long> tagIds = List.of(1L, 2L);

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            FamilyMember targetMember = createMember(memberId, familyId, 200L, "홍길동");
            FamilyMember currentMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag tag1 = createTag(1L, familyId, "친가");
            FamilyMemberTag tag2 = createTag(2L, familyId, "외가");

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(currentMember));
            when(findFamilyMemberPort.findById(memberId)).thenReturn(Optional.of(targetMember));
            when(findFamilyMemberTagPort.findAllByIds(tagIds)).thenReturn(List.of(tag1, tag2));

            // when
            FamilyMemberTagMappingInfo result = sut.modify(command, currentUserId);

            // then
            assertThat(result.memberId()).isEqualTo(memberId);
            assertThat(result.memberName()).isEqualTo("홍길동");
            assertThat(result.tags()).hasSize(2);

            verify(deleteFamilyMemberTagMappingPort).deleteAllByMemberId(memberId);
            verify(saveFamilyMemberTagMappingPort).saveAll(anyList());
        }

        @Test
        @DisplayName("빈 목록으로 모든 태그를 해제합니다")
        void modify_removes_all_tags_when_empty_list() {
            // given
            Long familyId = 1L;
            Long memberId = 10L;
            Long currentUserId = 100L;
            List<Long> tagIds = Collections.emptyList();

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            FamilyMember targetMember = createMember(memberId, familyId, 200L, "홍길동");
            FamilyMember currentMember = createOwnerMember(familyId, currentUserId);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(currentMember));
            when(findFamilyMemberPort.findById(memberId)).thenReturn(Optional.of(targetMember));

            // when
            FamilyMemberTagMappingInfo result = sut.modify(command, currentUserId);

            // then
            assertThat(result.memberId()).isEqualTo(memberId);
            assertThat(result.tags()).isEmpty();

            verify(deleteFamilyMemberTagMappingPort).deleteAllByMemberId(memberId);
            verify(saveFamilyMemberTagMappingPort, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("전체 교체 방식으로 태그를 변경합니다")
        void modify_replaces_all_tags() {
            // given
            Long familyId = 1L;
            Long memberId = 10L;
            Long currentUserId = 100L;
            List<Long> tagIds = List.of(3L); // 새 태그만

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            FamilyMember targetMember = createMember(memberId, familyId, 200L, "홍길동");
            FamilyMember currentMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag tag3 = createTag(3L, familyId, "조카들");

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(currentMember));
            when(findFamilyMemberPort.findById(memberId)).thenReturn(Optional.of(targetMember));
            when(findFamilyMemberTagPort.findAllByIds(tagIds)).thenReturn(List.of(tag3));

            // when
            FamilyMemberTagMappingInfo result = sut.modify(command, currentUserId);

            // then
            assertThat(result.tags()).hasSize(1);
            assertThat(result.tags().get(0).name()).isEqualTo("조카들");

            // 기존 매핑 삭제 후 새 매핑 저장
            verify(deleteFamilyMemberTagMappingPort).deleteAllByMemberId(memberId);
            verify(saveFamilyMemberTagMappingPort).saveAll(anyList());
        }
    }

    @Nested
    @DisplayName("권한 검증 테스트")
    class AuthorizationTest {

        @Test
        @DisplayName("OWNER가 아니면 F010 예외가 발생합니다")
        void modify_throws_F010_when_not_owner() {
            // given
            Long familyId = 1L;
            Long memberId = 10L;
            Long currentUserId = 100L;
            List<Long> tagIds = List.of(1L);

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

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
        @DisplayName("현재 사용자가 Family 구성원이 아니면 F002 예외가 발생합니다")
        void modify_throws_F002_when_not_family_member() {
            // given
            Long familyId = 1L;
            Long memberId = 10L;
            Long currentUserId = 100L;
            List<Long> tagIds = List.of(1L);

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.NOT_FAMILY_MEMBER.getCode());
                });
        }
    }

    @Nested
    @DisplayName("멤버 검증 테스트")
    class MemberValidationTest {

        @Test
        @DisplayName("대상 멤버가 존재하지 않으면 F009 예외가 발생합니다")
        void modify_throws_F009_when_member_not_found() {
            // given
            Long familyId = 1L;
            Long memberId = 999L;
            Long currentUserId = 100L;
            List<Long> tagIds = List.of(1L);

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            FamilyMember currentMember = createOwnerMember(familyId, currentUserId);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(currentMember));
            when(findFamilyMemberPort.findById(memberId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.MEMBER_NOT_FOUND.getCode());
                });
        }

        @Test
        @DisplayName("대상 멤버가 다른 Family 소속이면 T007 예외가 발생합니다")
        void modify_throws_T007_when_member_belongs_to_other_family() {
            // given
            Long familyId = 1L;
            Long otherFamilyId = 2L;
            Long memberId = 10L;
            Long currentUserId = 100L;
            List<Long> tagIds = List.of(1L);

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            FamilyMember currentMember = createOwnerMember(familyId, currentUserId);
            FamilyMember memberInOtherFamily = createMember(memberId, otherFamilyId, 200L, "홍길동");

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(currentMember));
            when(findFamilyMemberPort.findById(memberId)).thenReturn(Optional.of(memberInOtherFamily));

            // when & then
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.MEMBER_NOT_IN_FAMILY.getCode());
                });
        }
    }

    @Nested
    @DisplayName("태그 검증 테스트")
    class TagValidationTest {

        @Test
        @DisplayName("존재하지 않는 태그가 있으면 T005 예외가 발생합니다")
        void modify_throws_T005_when_tag_not_found() {
            // given
            Long familyId = 1L;
            Long memberId = 10L;
            Long currentUserId = 100L;
            List<Long> tagIds = List.of(1L, 999L); // 999L은 존재하지 않음

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            FamilyMember targetMember = createMember(memberId, familyId, 200L, "홍길동");
            FamilyMember currentMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag tag1 = createTag(1L, familyId, "친가");

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(currentMember));
            when(findFamilyMemberPort.findById(memberId)).thenReturn(Optional.of(targetMember));
            when(findFamilyMemberTagPort.findAllByIds(tagIds)).thenReturn(List.of(tag1)); // 1개만 반환

            // when & then
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.TAG_NOT_FOUND.getCode());
                });
        }

        @Test
        @DisplayName("다른 Family의 태그가 있으면 T006 예외가 발생합니다")
        void modify_throws_T006_when_tag_belongs_to_other_family() {
            // given
            Long familyId = 1L;
            Long otherFamilyId = 2L;
            Long memberId = 10L;
            Long currentUserId = 100L;
            List<Long> tagIds = List.of(1L, 2L);

            ModifyFamilyMemberTagMappingCommand command = new ModifyFamilyMemberTagMappingCommand(familyId, memberId, tagIds);

            FamilyMember targetMember = createMember(memberId, familyId, 200L, "홍길동");
            FamilyMember currentMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag tag1 = createTag(1L, familyId, "친가");
            FamilyMemberTag tagFromOtherFamily = createTag(2L, otherFamilyId, "외가");

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(currentMember));
            when(findFamilyMemberPort.findById(memberId)).thenReturn(Optional.of(targetMember));
            when(findFamilyMemberTagPort.findAllByIds(tagIds)).thenReturn(List.of(tag1, tagFromOtherFamily));

            // when & then
            assertThatThrownBy(() -> sut.modify(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getCode()).isEqualTo(FamilyExceptionCode.TAG_NOT_IN_FAMILY.getCode());
                });
        }
    }

    // Helper methods
    private FamilyMember createOwnerMember(Long familyId, Long userId) {
        return FamilyMember.withId(
            1L, familyId, userId, "OWNER유저", null, null, null, null, null,
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

    private FamilyMember createMember(Long memberId, Long familyId, Long userId, String name) {
        return FamilyMember.withId(
            memberId, familyId, userId, name, null, null, null, null, null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            userId, LocalDateTime.now(), null, null
        );
    }

    private FamilyMemberTag createTag(Long id, Long familyId, String name) {
        return FamilyMemberTag.withId(
            id, familyId, name, "#D3E5EF", 100L, LocalDateTime.now(), null, null
        );
    }
}
