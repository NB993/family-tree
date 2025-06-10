package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyByNameContainingQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FindFamilyService의 단위 테스트 클래스.
 * 
 * <p>UseCase 표준화에 따라 find(Query), findAll(Query) 메서드를 테스트합니다.</p>
 */
@DisplayName("[Unit Test] FindFamilyServiceTest")
@ExtendWith(MockitoExtension.class)
public class FindFamilyServiceTest {

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @InjectMocks
    private FindFamilyService sut;

    @Test
    @DisplayName("find 메서드는 유효한 ID로 Family를 찾아 반환해야 한다")
    void given_valid_query_when_find_then_return_family() {
        // given
        Long familyId = 1L;
        Long currentUserId = 1L;
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(familyId, currentUserId);
        Family expectedFamily = Family.withId(
            familyId,
            "name",
            "description",
            "profile",
            true, // 공개 Family로 설정
            2L,
            LocalDateTime.now(),
            2L,
            LocalDateTime.now()
        );

        when(findFamilyPort.findById(familyId)).thenReturn(Optional.of(expectedFamily));
        // 공개 Family이므로 FamilyMember 조회는 Mock 불필요 (validateFamilyAccessPermission에서 바로 return)

        // when
        Family family = sut.find(query);

        // then
        assertThat(family).isNotNull();
        assertThat(family.getId()).isEqualTo(expectedFamily.getId());
        assertThat(family.getName()).isEqualTo(expectedFamily.getName());
        assertThat(family.getDescription()).isEqualTo(expectedFamily.getDescription());
        assertThat(family.getProfileUrl()).isEqualTo(expectedFamily.getProfileUrl());
        assertThat(family.getCreatedBy()).isEqualTo(expectedFamily.getCreatedBy());
        assertThat(family.getCreatedAt()).isEqualTo(expectedFamily.getCreatedAt());
        assertThat(family.getModifiedBy()).isEqualTo(expectedFamily.getModifiedBy());
        assertThat(family.getModifiedAt()).isEqualTo(expectedFamily.getModifiedAt());
    }

    @Test
    @DisplayName("find 메서드는 query가 null이면 예외를 발생시켜야 한다")
    void given_null_query_when_find_then_throw_exception() {
        // given
        FindFamilyByIdQuery query = null;

        // when & then
        assertThatThrownBy(() -> sut.find(query))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }

    @Test
    @DisplayName("find 메서드는 ID와 일치하는 Family를 찾지 못한 경우 예외를 발생시켜야 한다")
    void given_invalid_id_when_find_then_throw_exception() {
        // given
        Long notSavedFamilyId = 999L;
        Long currentUserId = 1L;
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(notSavedFamilyId, currentUserId);

        // when
        when(findFamilyPort.findById(notSavedFamilyId))
            .thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> sut.find(query))
            .isInstanceOf(FTException.class)
            .hasMessage("대상을 찾지 못했습니다.");
    }

    @Test
    @DisplayName("findAll 메서드는 query가 null이라면 예외를 발생시켜야 한다")
    void given_null_query_when_find_all_then_throw_exception() {
        // given
        FindFamilyByNameContainingQuery query = null;

        // when & then
        assertThatThrownBy(() -> sut.findAll(query))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }

    @Test
    @DisplayName("findAll 메서드는 전달받은 name을 포함하는 Family를 찾지 못한 경우 빈 List를 리턴해야 한다")
    void given_non_matching_name_when_find_all_then_return_empty_list() {
        // given
        FindFamilyByNameContainingQuery query = new FindFamilyByNameContainingQuery("어느 Family도 포함하지 않는 이름");

        // when
        when(findFamilyPort.findByNameContaining(query.getName()))
            .thenReturn(List.of());

        // then
        List<Family> families = sut.findAll(query);
        assertThat(families).isEmpty();
    }

    @Test
    @DisplayName("findAll 메서드는 전달받은 name을 포함하는 Family를 찾아서 반환해야 한다")
    void given_matching_name_when_find_all_then_return_family_list() {
        // given
        FindFamilyByNameContainingQuery query = new FindFamilyByNameContainingQuery("가족");

        Family family = Family.newFamily("가족 이름1", "설명", "프로필 url", true);
        Family family2 = Family.newFamily("가족 이름2", "설명", "프로필 url", true);

        // when
        when(findFamilyPort.findByNameContaining(query.getName()))
            .thenReturn(List.of(family, family2));

        // then
        List<Family> families = sut.findAll(query);
        assertThat(families).hasSize(2);
        assertThat(families).extracting("name")
            .containsExactlyInAnyOrder("가족 이름1", "가족 이름2");
    }

    @Test
    @DisplayName("FindFamilyByIdQuery는 null ID로 생성할 수 없다")
    void given_null_id_when_create_query_then_throw_exception() {
        // given
        Long nullId = null;
        Long currentUserId = 1L;

        // when & then
        assertThatThrownBy(() -> new FindFamilyByIdQuery(nullId, currentUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family ID must not be null");
    }

    @Test
    @DisplayName("FindFamilyByIdQuery는 유효한 ID로 생성할 수 있다")
    void given_valid_id_when_create_query_then_success() {
        // given
        Long validId = 1L;
        Long currentUserId = 1L;

        // when
        FindFamilyByIdQuery query = new FindFamilyByIdQuery(validId, currentUserId);

        // then
        assertThat(query.id()).isEqualTo(validId);
        assertThat(query.currentUserId()).isEqualTo(currentUserId);
    }
}
