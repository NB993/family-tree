package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberByIdQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyValidationService;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.test.fixture.FamilyMemberFixture;
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
 * FindFamilyMemberService 클래스의 단위 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FindFamilyMemberServiceTest")
class FindFamilyMemberServiceTest {

    @InjectMocks
    private FindFamilyMemberService findFamilyMemberService;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;
    
    @Mock
    private FamilyValidationService familyValidationService;

    @Test
    @DisplayName("일반 구성원이 조회할 때 ACTIVE 상태 구성원만 나이순으로 반환합니다")
    void return_active_members_sorted_by_age_when_member_requests() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query = 
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);

        // 현재 사용자 (MEMBER 권한)
        FamilyMember currentMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            2L, familyId, currentUserId, FamilyMemberRole.MEMBER, "현재사용자",
            LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        // 다른 구성원들 (나이 순서: 막내 -> 둘째 -> 첫째)
        FamilyMember youngest = FamilyMemberFixture.withIdRoleNameAndBirthday(
            3L, familyId, 3L, FamilyMemberRole.MEMBER, "막내",
            LocalDateTime.of(2000, 3, 10, 0, 0)
        );

        FamilyMember middle = FamilyMemberFixture.withIdRoleNameAndBirthday(
            4L, familyId, 4L, FamilyMemberRole.MEMBER, "둘째",
            LocalDateTime.of(1995, 8, 20, 0, 0)
        );

        FamilyMember oldest = FamilyMemberFixture.withIdRoleNameAndBirthday(
            5L, familyId, 5L, FamilyMemberRole.MEMBER, "첫째",
            LocalDateTime.of(1985, 12, 5, 0, 0)
        );

        FamilyMember suspended = FamilyMemberFixture.withIdFullAndBirthday(
            6L, familyId, 6L, FamilyMemberRole.MEMBER, FamilyMemberStatus.SUSPENDED, "정지된사용자",
            LocalDateTime.of(1992, 1, 1, 0, 0)
        );

        List<FamilyMember> allMembers = List.of(oldest, middle, currentMember, youngest, suspended);

        // Mocking: 현재 사용자 조회
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // Mocking: 전체 구성원 조회
        when(findFamilyMemberPort.findAllByFamilyId(familyId))
            .thenReturn(allMembers);

        // when
        List<FamilyMember> result = findFamilyMemberService.findAll(query);

        // then
        assertThat(result).hasSize(4); // SUSPENDED 제외
        assertThat(result.get(0).getName()).isEqualTo("막내"); // 가장 어림 (2000년생)
        assertThat(result.get(1).getName()).isEqualTo("둘째"); // 1995년생
        assertThat(result.get(2).getName()).isEqualTo("현재사용자"); // 1990년생
        assertThat(result.get(3).getName()).isEqualTo("첫째"); // 가장 나이 많음 (1985년생)

        // SUSPENDED 사용자는 결과에 포함되지 않음
        assertThat(result.stream().noneMatch(member -> member.getStatus() == FamilyMemberStatus.SUSPENDED))
            .isTrue();
    }

    @Test
    @DisplayName("ADMIN 권한 사용자가 조회할 때 SUSPENDED 구성원도 포함하여 반환합니다")
    void return_all_members_including_suspended_when_admin_requests() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query = 
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);

        // 현재 사용자 (ADMIN 권한)
        FamilyMember adminMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            2L, familyId, currentUserId, FamilyMemberRole.ADMIN, "관리자",
            LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        FamilyMember suspendedMember = FamilyMemberFixture.withIdFullAndBirthday(
            3L, familyId, 3L, FamilyMemberRole.MEMBER, FamilyMemberStatus.SUSPENDED, "정지된사용자",
            LocalDateTime.of(1992, 1, 1, 0, 0)
        );

        List<FamilyMember> allMembers = List.of(adminMember, suspendedMember);

        // Mocking: 현재 사용자 조회 (ADMIN)
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(adminMember));

        // Mocking: 전체 구성원 조회
        when(findFamilyMemberPort.findAllByFamilyId(familyId))
            .thenReturn(allMembers);

        // when
        List<FamilyMember> result = findFamilyMemberService.findAll(query);

        // then
        assertThat(result).hasSize(2); // ADMIN은 SUSPENDED도 볼 수 있음
        assertThat(result.stream().anyMatch(member -> member.getStatus() == FamilyMemberStatus.SUSPENDED))
            .isTrue();
    }

    @Test
    @DisplayName("생일 정보가 없는 구성원은 목록 맨 뒤에 표시됩니다")
    void members_without_birthday_appear_at_end() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query = 
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);

        FamilyMember currentMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            2L, familyId, currentUserId, FamilyMemberRole.MEMBER, "현재사용자",
            LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        FamilyMember memberWithoutBirthday = FamilyMemberFixture.withIdRoleNameAndBirthday(
            3L, familyId, 3L, FamilyMemberRole.MEMBER, "생일없음", null
        );

        FamilyMember youngMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            4L, familyId, 4L, FamilyMemberRole.MEMBER, "어린사용자",
            LocalDateTime.of(2000, 1, 1, 0, 0)
        );

        List<FamilyMember> allMembers = List.of(memberWithoutBirthday, currentMember, youngMember);

        // Mocking: 현재 사용자 조회
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // Mocking: 전체 구성원 조회
        when(findFamilyMemberPort.findAllByFamilyId(familyId))
            .thenReturn(allMembers);

        // when
        List<FamilyMember> result = findFamilyMemberService.findAll(query);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("어린사용자"); // 가장 어림 (2000년생)
        assertThat(result.get(1).getName()).isEqualTo("현재사용자"); // 1990년생
        assertThat(result.get(2).getName()).isEqualTo("생일없음"); // 생일 정보 없음 - 맨 뒤
    }

    @Test
    @DisplayName("현재 사용자가 Family 구성원이 아닐 때 FTException이 발생합니다")
    void throw_exception_when_user_is_not_family_member() {
        // given
        Long familyId = 1L;
        Long currentUserId = 999L; // 존재하지 않는 사용자
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query = 
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);

        // Mocking: 현재 사용자가 Family 구성원이 아님
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> findFamilyMemberService.findAll(query))
            .isInstanceOf(FTException.class);
    }

    @Test
    @DisplayName("query가 null일 때 NullPointerException이 발생합니다")
    void throw_exception_when_query_is_null() {
        // when & then
        assertThatThrownBy(() -> findFamilyMemberService.findAll(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }

    @Test
    @DisplayName("Family에 구성원이 없을 때 빈 리스트를 반환합니다")
    void return_empty_list_when_no_members() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query = 
            new FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery(familyId, currentUserId);

        FamilyMember currentMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            2L, familyId, currentUserId, FamilyMemberRole.MEMBER, "현재사용자",
            LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        // Mocking: 현재 사용자 조회
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // Mocking: 현재 사용자만 있는 경우
        when(findFamilyMemberPort.findAllByFamilyId(familyId))
            .thenReturn(List.of(currentMember));

        // when
        List<FamilyMember> result = findFamilyMemberService.findAll(query);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(currentMember);
    }

    @Test
    @DisplayName("단건 조회 시 대상 구성원을 성공적으로 반환합니다")
    void return_target_member_when_find_single_member() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        Long targetMemberId = 3L;
        FindFamilyMemberByIdQuery query = new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId);

        FamilyMember currentMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            2L, familyId, currentUserId, FamilyMemberRole.MEMBER, "현재사용자",
            LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        FamilyMember targetMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            3L, familyId, 3L, FamilyMemberRole.MEMBER, "대상구성원",
            LocalDateTime.of(1995, 8, 20, 0, 0)
        );

        // Mocking: 현재 사용자 조회
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // Mocking: 대상 구성원 조회
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(targetMember));

        // when
        FamilyMember result = findFamilyMemberService.find(query);

        // then
        assertThat(result).isEqualTo(targetMember);
        assertThat(result.getName()).isEqualTo("대상구성원");
    }

    @Test
    @DisplayName("단건 조회 시 일반 사용자가 SUSPENDED 구성원 조회할 때 FTException이 발생합니다")
    void throw_exception_when_member_tries_to_find_suspended_member() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        Long targetMemberId = 3L;
        FindFamilyMemberByIdQuery query = new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId);

        FamilyMember currentMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            2L, familyId, currentUserId, FamilyMemberRole.MEMBER, "현재사용자",
            LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        FamilyMember suspendedMember = FamilyMemberFixture.withIdFullAndBirthday(
            3L, familyId, 3L, FamilyMemberRole.MEMBER, FamilyMemberStatus.SUSPENDED, "정지된구성원",
            LocalDateTime.of(1995, 8, 20, 0, 0)
        );

        // Mocking: 현재 사용자 조회
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // Mocking: SUSPENDED 구성원 조회
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(suspendedMember));

        // when & then
        assertThatThrownBy(() -> findFamilyMemberService.find(query))
            .isInstanceOf(FTException.class);
    }

    @Test
    @DisplayName("단건 조회 시 ADMIN이 SUSPENDED 구성원 조회하면 성공적으로 반환합니다")
    void return_suspended_member_when_admin_requests() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        Long targetMemberId = 3L;
        FindFamilyMemberByIdQuery query = new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId);

        FamilyMember adminMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            2L, familyId, currentUserId, FamilyMemberRole.ADMIN, "관리자",
            LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        FamilyMember suspendedMember = FamilyMemberFixture.withIdFullAndBirthday(
            3L, familyId, 3L, FamilyMemberRole.MEMBER, FamilyMemberStatus.SUSPENDED, "정지된구성원",
            LocalDateTime.of(1995, 8, 20, 0, 0)
        );

        // Mocking: ADMIN 사용자 조회
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(adminMember));

        // Mocking: SUSPENDED 구성원 조회
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.of(suspendedMember));

        // when
        FamilyMember result = findFamilyMemberService.find(query);

        // then
        assertThat(result).isEqualTo(suspendedMember);
        assertThat(result.getStatus()).isEqualTo(FamilyMemberStatus.SUSPENDED);
    }

    @Test
    @DisplayName("단건 조회 시 존재하지 않는 구성원 조회할 때 FTException이 발생합니다")
    void throw_exception_when_target_member_not_found() {
        // given
        Long familyId = 1L;
        Long currentUserId = 2L;
        Long targetMemberId = 999L; // 존재하지 않는 구성원
        FindFamilyMemberByIdQuery query = new FindFamilyMemberByIdQuery(familyId, currentUserId, targetMemberId);

        FamilyMember currentMember = FamilyMemberFixture.withIdRoleNameAndBirthday(
            2L, familyId, currentUserId, FamilyMemberRole.MEMBER, "현재사용자",
            LocalDateTime.of(1990, 5, 15, 0, 0)
        );

        // Mocking: 현재 사용자 조회
        when(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .thenReturn(Optional.of(currentMember));

        // Mocking: 대상 구성원이 존재하지 않음
        when(findFamilyMemberPort.findById(targetMemberId))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> findFamilyMemberService.find(query))
            .isInstanceOf(FTException.class);
    }

    @Test
    @DisplayName("단건 조회 시 query가 null일 때 NullPointerException이 발생합니다")
    void throw_exception_when_find_query_is_null() {
        // when & then
        assertThatThrownBy(() -> findFamilyMemberService.find(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }
}
