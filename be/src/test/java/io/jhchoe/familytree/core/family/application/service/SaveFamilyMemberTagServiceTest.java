package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyMemberTagCommand;
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
 * SaveFamilyMemberTagService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] SaveFamilyMemberTagServiceTest")
class SaveFamilyMemberTagServiceTest {

    @InjectMocks
    private SaveFamilyMemberTagService saveFamilyMemberTagService;

    @Mock
    private SaveFamilyMemberTagPort saveFamilyMemberTagPort;

    @Mock
    private FindFamilyMemberTagPort findFamilyMemberTagPort;

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Nested
    @DisplayName("태그 생성 테스트")
    class SaveTagTest {

        @Test
        @DisplayName("유효한 요청으로 태그 생성 시 ID를 반환합니다")
        void save_returns_id_when_valid_request() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            SaveFamilyMemberTagCommand command = new SaveFamilyMemberTagCommand(familyId, "친가");

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);

            // Mocking: Family 존재
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            // Mocking: 현재 사용자가 Family의 OWNER
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            // Mocking: 태그 수 제한 미초과
            when(findFamilyMemberTagPort.countByFamilyId(familyId)).thenReturn(5);
            // Mocking: 이름 중복 없음
            when(findFamilyMemberTagPort.findByFamilyIdAndName(familyId, "친가"))
                .thenReturn(Optional.empty());
            // Mocking: 저장 성공
            when(saveFamilyMemberTagPort.save(any(FamilyMemberTag.class))).thenReturn(1L);

            // when
            Long savedId = saveFamilyMemberTagService.save(command, currentUserId);

            // then
            assertThat(savedId).isEqualTo(1L);
        }

        @Test
        @DisplayName("OWNER가 아닌 경우 F010 예외가 발생합니다")
        void save_throws_F010_when_not_owner() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            SaveFamilyMemberTagCommand command = new SaveFamilyMemberTagCommand(familyId, "친가");

            FamilyMember memberOnly = createMemberOnly(familyId, currentUserId);

            // Mocking: Family 존재
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            // Mocking: 현재 사용자가 MEMBER (OWNER 아님)
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(memberOnly));

            // when & then
            assertThatThrownBy(() -> saveFamilyMemberTagService.save(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getExceptionCode()).isEqualTo(FamilyExceptionCode.NOT_AUTHORIZED);
                });
        }

        @Test
        @DisplayName("태그 수가 10개 이상이면 T001 예외가 발생합니다")
        void save_throws_T001_when_tag_count_exceeds_10() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            SaveFamilyMemberTagCommand command = new SaveFamilyMemberTagCommand(familyId, "새태그");

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);

            // Mocking: Family 존재
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            // Mocking: 현재 사용자가 OWNER
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            // Mocking: 태그 수 제한 초과 (이미 10개)
            when(findFamilyMemberTagPort.countByFamilyId(familyId)).thenReturn(10);

            // when & then
            assertThatThrownBy(() -> saveFamilyMemberTagService.save(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getExceptionCode()).isEqualTo(FamilyExceptionCode.TAG_LIMIT_EXCEEDED);
                });
        }

        @Test
        @DisplayName("이름이 중복되면 T002 예외가 발생합니다")
        void save_throws_T002_when_name_duplicated() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            SaveFamilyMemberTagCommand command = new SaveFamilyMemberTagCommand(familyId, "친가");

            FamilyMember ownerMember = createOwnerMember(familyId, currentUserId);
            FamilyMemberTag existingTag = FamilyMemberTag.withId(
                1L, familyId, "친가", "#D3E5EF", currentUserId, LocalDateTime.now(), null, null
            );

            // Mocking: Family 존재
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            // Mocking: 현재 사용자가 OWNER
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(ownerMember));
            // Mocking: 태그 수 제한 미초과
            when(findFamilyMemberTagPort.countByFamilyId(familyId)).thenReturn(5);
            // Mocking: 이름 중복 존재
            when(findFamilyMemberTagPort.findByFamilyIdAndName(familyId, "친가"))
                .thenReturn(Optional.of(existingTag));

            // when & then
            assertThatThrownBy(() -> saveFamilyMemberTagService.save(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getExceptionCode()).isEqualTo(FamilyExceptionCode.TAG_NAME_DUPLICATED);
                });
        }

        @Test
        @DisplayName("Family 구성원이 아니면 F001 예외가 발생합니다")
        void save_throws_F001_when_not_family_member() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            SaveFamilyMemberTagCommand command = new SaveFamilyMemberTagCommand(familyId, "친가");

            // Mocking: Family 존재
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            // Mocking: 현재 사용자가 Family 구성원이 아님
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> saveFamilyMemberTagService.save(command, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getExceptionCode()).isEqualTo(FamilyExceptionCode.NOT_FAMILY_MEMBER);
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
