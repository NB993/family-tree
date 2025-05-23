package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMembersRoleQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FindFamilyMemberRoleService 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] FindFamilyMemberRoleServiceTest")
@ExtendWith(MockitoExtension.class)
class FindFamilyMemberRoleServiceTest {

    @InjectMocks
    private FindFamilyMemberRoleService sut;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Test
    @DisplayName("Family 구성원이 요청한 경우 모든 구성원의 역할 정보를 조회할 수 있습니다")
    void find_all_family_members_roles_by_family_member() {
        // given
        Long familyId = 1L;
        Long currentUserId = 10L;
        
        // 현재 사용자는 Family 구성원
        FamilyMember currentMember = createFamilyMember(15L, familyId, currentUserId, FamilyMemberRole.MEMBER);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.of(currentMember));
        
        // Family의 모든 구성원 목록
        List<FamilyMember> familyMembers = Arrays.asList(
            createFamilyMember(1L, familyId, 100L, FamilyMemberRole.OWNER),
            createFamilyMember(2L, familyId, 200L, FamilyMemberRole.ADMIN),
            createFamilyMember(3L, familyId, 300L, FamilyMemberRole.MEMBER)
        );
        given(findFamilyMemberPort.findAllByFamilyId(familyId))
            .willReturn(familyMembers);
        
        FindFamilyMembersRoleQuery query = new FindFamilyMembersRoleQuery(familyId, currentUserId);

        // when
        List<FamilyMember> result = sut.findAllByFamilyId(query);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(FamilyMember::getRole)
            .containsExactly(FamilyMemberRole.OWNER, FamilyMemberRole.ADMIN, FamilyMemberRole.MEMBER);
        
        // findFamilyMemberPort 호출 검증
        then(findFamilyMemberPort).should().findByFamilyIdAndUserId(familyId, currentUserId);
        then(findFamilyMemberPort).should().findAllByFamilyId(familyId);
    }

    @Test
    @DisplayName("현재 사용자가 Family 구성원이 아닌 경우 예외가 발생합니다")
    void throw_exception_when_current_user_is_not_family_member() {
        // given
        Long familyId = 1L;
        Long currentUserId = 10L;
        
        // 현재 사용자가 Family 구성원이 아님
        given(findFamilyMemberPort.findByFamilyIdAndUserId(familyId, currentUserId))
            .willReturn(Optional.empty());
        
        FindFamilyMembersRoleQuery query = new FindFamilyMembersRoleQuery(familyId, currentUserId);

        // when & then
        assertThatThrownBy(() -> sut.findAllByFamilyId(query))
            .isInstanceOf(FTException.class)
            .extracting("exceptionCodeType")
            .isEqualTo(FamilyExceptionCode.NOT_FAMILY_MEMBER);
        
        // findAllByFamilyId는 호출되지 않아야 함
        then(findFamilyMemberPort).should(never()).findAllByFamilyId(any());
    }

    @Test
    @DisplayName("query가 null인 경우 예외가 발생합니다")
    void throw_exception_when_query_is_null() {
        // given
        FindFamilyMembersRoleQuery query = null;

        // when & then
        assertThatThrownBy(() -> sut.findAllByFamilyId(query))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }

    /**
     * 테스트용 FamilyMember 객체를 생성합니다.
     */
    private FamilyMember createFamilyMember(Long id, Long familyId, Long userId, FamilyMemberRole role) {
        return FamilyMember.existingMember(
            id, familyId, userId, "Test User", "profile.jpg",
            LocalDateTime.now(), "KR", FamilyMemberStatus.ACTIVE, role,
            1L, LocalDateTime.now(), 1L, LocalDateTime.now()
        );
    }
}
