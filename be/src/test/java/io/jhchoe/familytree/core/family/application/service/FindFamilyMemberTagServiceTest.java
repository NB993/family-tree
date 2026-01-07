package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagInfo;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberTagsQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagMappingPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberTagPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
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
 * FindFamilyMemberTagService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FindFamilyMemberTagServiceTest")
class FindFamilyMemberTagServiceTest {

    @InjectMocks
    private FindFamilyMemberTagService findFamilyMemberTagService;

    @Mock
    private FindFamilyMemberTagPort findFamilyMemberTagPort;

    @Mock
    private FindFamilyMemberTagMappingPort findFamilyMemberTagMappingPort;

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Nested
    @DisplayName("태그 목록 조회 테스트")
    class FindAllTagsTest {

        @Test
        @DisplayName("태그 목록과 각 태그의 멤버 수를 반환합니다")
        void findAll_returns_tags_with_member_count() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            FindFamilyMemberTagsQuery query = new FindFamilyMemberTagsQuery(familyId);

            FamilyMember member = createMember(familyId, currentUserId);
            FamilyMemberTag tag1 = FamilyMemberTag.withId(
                1L, familyId, "외가", "#D3E5EF", currentUserId, LocalDateTime.now(), null, null
            );
            FamilyMemberTag tag2 = FamilyMemberTag.withId(
                2L, familyId, "친가", "#FFE2DD", currentUserId, LocalDateTime.now(), null, null
            );

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(member));
            when(findFamilyMemberTagPort.findAllByFamilyId(familyId))
                .thenReturn(List.of(tag1, tag2));
            when(findFamilyMemberTagMappingPort.countByTagId(1L)).thenReturn(3);
            when(findFamilyMemberTagMappingPort.countByTagId(2L)).thenReturn(5);

            // when
            List<FamilyMemberTagInfo> result = findFamilyMemberTagService.findAll(query, currentUserId);

            // then
            assertThat(result).hasSize(2);
            // 가나다순 정렬 확인: 외가 > 친가
            assertThat(result.get(0).name()).isEqualTo("외가");
            assertThat(result.get(0).memberCount()).isEqualTo(3);
            assertThat(result.get(1).name()).isEqualTo("친가");
            assertThat(result.get(1).memberCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("태그가 없으면 빈 목록을 반환합니다")
        void findAll_returns_empty_list_when_no_tags() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            FindFamilyMemberTagsQuery query = new FindFamilyMemberTagsQuery(familyId);

            FamilyMember member = createMember(familyId, currentUserId);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(member));
            when(findFamilyMemberTagPort.findAllByFamilyId(familyId))
                .thenReturn(Collections.emptyList());

            // when
            List<FamilyMemberTagInfo> result = findFamilyMemberTagService.findAll(query, currentUserId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("가나다순으로 정렬됩니다")
        void findAll_returns_tags_sorted_by_name() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            FindFamilyMemberTagsQuery query = new FindFamilyMemberTagsQuery(familyId);

            FamilyMember member = createMember(familyId, currentUserId);
            FamilyMemberTag tagA = FamilyMemberTag.withId(
                1L, familyId, "하나", "#D3E5EF", currentUserId, LocalDateTime.now(), null, null
            );
            FamilyMemberTag tagB = FamilyMemberTag.withId(
                2L, familyId, "가나", "#FFE2DD", currentUserId, LocalDateTime.now(), null, null
            );
            FamilyMemberTag tagC = FamilyMemberTag.withId(
                3L, familyId, "다라", "#DBEDDB", currentUserId, LocalDateTime.now(), null, null
            );

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.of(member));
            when(findFamilyMemberTagPort.findAllByFamilyId(familyId))
                .thenReturn(List.of(tagA, tagB, tagC)); // 정렬 안 된 상태
            when(findFamilyMemberTagMappingPort.countByTagId(1L)).thenReturn(0);
            when(findFamilyMemberTagMappingPort.countByTagId(2L)).thenReturn(0);
            when(findFamilyMemberTagMappingPort.countByTagId(3L)).thenReturn(0);

            // when
            List<FamilyMemberTagInfo> result = findFamilyMemberTagService.findAll(query, currentUserId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).name()).isEqualTo("가나");
            assertThat(result.get(1).name()).isEqualTo("다라");
            assertThat(result.get(2).name()).isEqualTo("하나");
        }

        @Test
        @DisplayName("Family 구성원이 아니면 F001 예외가 발생합니다")
        void findAll_throws_F001_when_not_family_member() {
            // given
            Long familyId = 1L;
            Long currentUserId = 100L;
            FindFamilyMemberTagsQuery query = new FindFamilyMemberTagsQuery(familyId);

            // Mocking
            when(findFamilyPort.existsById(familyId)).thenReturn(true);
            when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
                .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> findFamilyMemberTagService.findAll(query, currentUserId))
                .isInstanceOf(FTException.class)
                .satisfies(ex -> {
                    FTException ftEx = (FTException) ex;
                    assertThat(ftEx.getExceptionCode()).isEqualTo(FamilyExceptionCode.NOT_FAMILY_MEMBER);
                });
        }
    }

    private FamilyMember createMember(Long familyId, Long userId) {
        return FamilyMember.withId(
            1L, familyId, userId, "테스트유저", null, null, null, null, null,
            FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            userId, LocalDateTime.now(), null, null
        );
    }
}
