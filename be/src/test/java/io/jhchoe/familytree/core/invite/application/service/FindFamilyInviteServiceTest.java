package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInviteByCodeQuery;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInviteByIdQuery;
import io.jhchoe.familytree.core.invite.application.port.in.FindFamilyInvitesByRequesterIdQuery;
import io.jhchoe.familytree.core.invite.application.port.out.FindFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.domain.FamilyInviteStatus;
import io.jhchoe.familytree.core.invite.exception.InviteExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] FindFamilyInviteServiceTest")
class FindFamilyInviteServiceTest {

    @InjectMocks
    private FindFamilyInviteService findFamilyInviteService;

    @Mock
    private FindFamilyInvitePort findFamilyInvitePort;

    @Test
    @DisplayName("초대 코드로 초대를 조회할 수 있습니다")
    void find_by_code_returns_invite() {
        // given
        String inviteCode = "test-code";
        FindFamilyInviteByCodeQuery query = new FindFamilyInviteByCodeQuery(inviteCode);
        FamilyInvite expectedInvite = FamilyInvite.withId(
            1L,
            1L,
            inviteCode,
            LocalDateTime.now().plusDays(1),
            10,
            0,
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // Mocking: 초대 코드로 조회 시 초대 반환
        when(findFamilyInvitePort.findByInviteCode(inviteCode)).thenReturn(Optional.of(expectedInvite));

        // when
        FamilyInvite actualInvite = findFamilyInviteService.find(query);

        // then
        assertThat(actualInvite).isEqualTo(expectedInvite);
    }

    @Test
    @DisplayName("존재하지 않는 초대 코드로 조회하면 예외가 발생합니다")
    void find_by_code_throws_exception_when_not_found() {
        // given
        String inviteCode = "non-existent-code";
        FindFamilyInviteByCodeQuery query = new FindFamilyInviteByCodeQuery(inviteCode);

        // Mocking: 초대 코드로 조회 시 빈 결과 반환
        when(findFamilyInvitePort.findByInviteCode(inviteCode)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> findFamilyInviteService.find(query))
            .isInstanceOf(FTException.class)
            .hasFieldOrPropertyWithValue("exceptionCodeType", InviteExceptionCode.INVITE_NOT_FOUND);
    }

    @Test
    @DisplayName("ID로 초대를 조회할 수 있습니다")
    void find_by_id_returns_invite() {
        // given
        Long inviteId = 1L;
        FindFamilyInviteByIdQuery query = new FindFamilyInviteByIdQuery(inviteId);
        FamilyInvite expectedInvite = FamilyInvite.withId(
            inviteId,
            1L,
            "test-code",
            LocalDateTime.now().plusDays(1),
            10,
            0,
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // Mocking: ID로 조회 시 초대 반환
        when(findFamilyInvitePort.findById(inviteId)).thenReturn(Optional.of(expectedInvite));

        // when
        FamilyInvite actualInvite = findFamilyInviteService.find(query);

        // then
        assertThat(actualInvite).isEqualTo(expectedInvite);
    }

    @Test
    @DisplayName("요청자 ID로 초대 목록을 조회할 수 있습니다")
    void findAll_by_requester_id_returns_invite_list() {
        // given
        Long requesterId = 1L;
        FindFamilyInvitesByRequesterIdQuery query = new FindFamilyInvitesByRequesterIdQuery(requesterId);
        List<FamilyInvite> expectedInvites = List.of(
            FamilyInvite.withId(1L, requesterId, "code1", LocalDateTime.now().plusDays(1),
                10, 0, FamilyInviteStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()),
            FamilyInvite.withId(2L, requesterId, "code2", LocalDateTime.now().plusDays(1),
                10,  0, FamilyInviteStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now())
        );

        // Mocking: 요청자 ID로 조회 시 초대 목록 반환
        when(findFamilyInvitePort.findByRequesterId(requesterId)).thenReturn(expectedInvites);

        // when
        List<FamilyInvite> actualInvites = findFamilyInviteService.findAll(query);

        // then
        assertThat(actualInvites).hasSize(2);
        assertThat(actualInvites).isEqualTo(expectedInvites);
    }

    @Test
    @DisplayName("query가 null이면 NPE가 발생합니다")
    void find_throws_npe_when_query_is_null() {
        // when & then
        assertThatThrownBy(() -> findFamilyInviteService.find((FindFamilyInviteByCodeQuery) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");

        assertThatThrownBy(() -> findFamilyInviteService.find((FindFamilyInviteByIdQuery) null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");

        assertThatThrownBy(() -> findFamilyInviteService.findAll(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }
}
