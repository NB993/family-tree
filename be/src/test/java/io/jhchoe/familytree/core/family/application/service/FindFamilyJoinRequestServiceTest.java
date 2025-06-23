package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyJoinRequestQuery;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyJoinRequestPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FindFamilyJoinRequestService")
class FindFamilyJoinRequestServiceTest {

    @Mock
    private FindFamilyJoinRequestPort findFamilyJoinRequestPort;
    
    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;
    
    @Mock
    private FindFamilyPort findFamilyPort;
    
    @InjectMocks
    private FindFamilyJoinRequestService findFamilyJoinRequestService;
    
    private FindFamilyJoinRequestQuery query;
    private FamilyMember adminMember;
    private FamilyMember ownerMember;
    private FamilyMember regularMember;
    private List<FamilyJoinRequest> joinRequests;

    @BeforeEach
    void setUp() {
        Long familyId = 1L;
        Long currentUserId = 2L;
        query = new FindFamilyJoinRequestQuery(familyId, currentUserId);
        
        adminMember = FamilyMember.existingMember(
            1L, familyId, currentUserId, "Admin User", "profile.jpg", 
            LocalDateTime.of(1990, 1, 1, 0, 0),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.ADMIN,
            null, null, null, null
        );
        
        ownerMember = FamilyMember.existingMember(
            2L, familyId, currentUserId, "Owner User", "profile.jpg", 
            LocalDateTime.of(1990, 1, 1, 0, 0),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.OWNER,
            null, null, null, null
        );
        
        regularMember = FamilyMember.existingMember(
            3L, familyId, currentUserId, "Regular User", "profile.jpg", 
            LocalDateTime.of(1990, 1, 1, 0, 0),
            "KR", FamilyMemberStatus.ACTIVE, FamilyMemberRole.MEMBER,
            null, null, null, null
        );
        
        joinRequests = List.of(
            FamilyJoinRequest.withId(1L, familyId, 3L, FamilyJoinRequestStatus.PENDING, 
                LocalDateTime.now(), null, null, null),
            FamilyJoinRequest.withId(2L, familyId, 4L, FamilyJoinRequestStatus.PENDING, 
                LocalDateTime.now(), null, null, null)
        );
    }

    @Test
    @DisplayName("ADMIN 권한을 가진 사용자가 가입 신청 목록을 성공적으로 조회할 수 있다")
    void should_find_join_requests_when_user_is_admin() {
        // given
        given(findFamilyPort.existsById(query.getFamilyId())).willReturn(true);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId()))
            .willReturn(Optional.of(adminMember));
        given(findFamilyJoinRequestPort.findAllByFamilyId(query.getFamilyId()))
            .willReturn(joinRequests);

        // when
        List<FamilyJoinRequest> result = findFamilyJoinRequestService.findAllByFamilyId(query);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(joinRequests);
        verify(findFamilyPort).existsById(query.getFamilyId());
        verify(findFamilyMemberPort).findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId());
        verify(findFamilyJoinRequestPort).findAllByFamilyId(query.getFamilyId());
    }

    @Test
    @DisplayName("OWNER 권한을 가진 사용자가 가입 신청 목록을 성공적으로 조회할 수 있다")
    void should_find_join_requests_when_user_is_owner() {
        // given
        given(findFamilyPort.existsById(query.getFamilyId())).willReturn(true);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId()))
            .willReturn(Optional.of(ownerMember));
        given(findFamilyJoinRequestPort.findAllByFamilyId(query.getFamilyId()))
            .willReturn(joinRequests);

        // when
        List<FamilyJoinRequest> result = findFamilyJoinRequestService.findAllByFamilyId(query);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(joinRequests);
        verify(findFamilyPort).existsById(query.getFamilyId());
        verify(findFamilyMemberPort).findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId());
        verify(findFamilyJoinRequestPort).findAllByFamilyId(query.getFamilyId());
    }

    @Test
    @DisplayName("query가 null이면 예외를 발생시킨다")
    void should_throw_exception_when_query_is_null() {
        // given
        FindFamilyJoinRequestQuery nullQuery = null;

        // when & then
        assertThatThrownBy(() -> findFamilyJoinRequestService.findAllByFamilyId(nullQuery))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }

    @Test
    @DisplayName("존재하지 않는 Family ID로 조회하면 FAMILY_NOT_FOUND 예외를 발생시킨다")
    void should_throw_family_not_found_exception_when_family_does_not_exist() {
        // given
        given(findFamilyPort.existsById(query.getFamilyId())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> findFamilyJoinRequestService.findAllByFamilyId(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.FAMILY_NOT_FOUND);
        
        verify(findFamilyPort).existsById(query.getFamilyId());
    }

    @Test
    @DisplayName("Family 구성원이 아닌 사용자가 조회하면 NOT_FAMILY_MEMBER 예외를 발생시킨다")
    void should_throw_not_family_member_exception_when_user_is_not_member() {
        // given
        given(findFamilyPort.existsById(query.getFamilyId())).willReturn(true);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId()))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> findFamilyJoinRequestService.findAllByFamilyId(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.NOT_FAMILY_MEMBER);
        
        verify(findFamilyPort).existsById(query.getFamilyId());
        verify(findFamilyMemberPort).findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId());
    }

    @Test
    @DisplayName("MEMBER 권한을 가진 사용자가 조회하면 NOT_AUTHORIZED 예외를 발생시킨다")
    void should_throw_not_authorized_exception_when_user_is_regular_member() {
        // given
        given(findFamilyPort.existsById(query.getFamilyId())).willReturn(true);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId()))
            .willReturn(Optional.of(regularMember));

        // when & then
        assertThatThrownBy(() -> findFamilyJoinRequestService.findAllByFamilyId(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.NOT_AUTHORIZED);
        
        verify(findFamilyPort).existsById(query.getFamilyId());
        verify(findFamilyMemberPort).findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId());
    }

    @Test
    @DisplayName("비활성화된 구성원이 조회하면 MEMBER_NOT_ACTIVE 예외를 발생시킨다")
    void should_throw_member_not_active_exception_when_member_is_inactive() {
        // given
        FamilyMember inactiveMember = FamilyMember.existingMember(
            1L, query.getFamilyId(), query.getCurrentUserId(), "Inactive Admin", "profile.jpg", 
            LocalDateTime.of(1990, 1, 1, 0, 0),
            "KR", FamilyMemberStatus.SUSPENDED, FamilyMemberRole.ADMIN,
            null, null, null, null
        );
        
        given(findFamilyPort.existsById(query.getFamilyId())).willReturn(true);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId()))
            .willReturn(Optional.of(inactiveMember));

        // when & then
        assertThatThrownBy(() -> findFamilyJoinRequestService.findAllByFamilyId(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", FamilyExceptionCode.MEMBER_NOT_ACTIVE);
        
        verify(findFamilyPort).existsById(query.getFamilyId());
        verify(findFamilyMemberPort).findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId());
    }

    @Test
    @DisplayName("가입 신청이 없는 경우 빈 목록을 반환한다")
    void should_return_empty_list_when_no_join_requests_exist() {
        // given
        given(findFamilyPort.existsById(query.getFamilyId())).willReturn(true);
        given(findFamilyMemberPort.findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId()))
            .willReturn(Optional.of(adminMember));
        given(findFamilyJoinRequestPort.findAllByFamilyId(query.getFamilyId()))
            .willReturn(List.of());

        // when
        List<FamilyJoinRequest> result = findFamilyJoinRequestService.findAllByFamilyId(query);

        // then
        assertThat(result).isEmpty();
        verify(findFamilyPort).existsById(query.getFamilyId());
        verify(findFamilyMemberPort).findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId());
        verify(findFamilyJoinRequestPort).findAllByFamilyId(query.getFamilyId());
    }
}
