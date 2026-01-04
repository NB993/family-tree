package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.invite.application.port.in.SaveFamilyInviteCommand;
import io.jhchoe.familytree.test.fixture.FamilyMemberFixture;
import io.jhchoe.familytree.core.invite.application.port.out.SaveFamilyInvitePort;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] SaveFamilyInviteServiceTest")
class SaveFamilyInviteServiceTest {

    @InjectMocks
    private SaveFamilyInviteService saveFamilyInviteService;

    @Mock
    private SaveFamilyInvitePort saveFamilyInvitePort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @Test
    @DisplayName("OWNER인 사용자가 초대를 생성하면 초대 코드를 반환합니다")
    void save_creates_invite_and_returns_invite_code() {
        // given
        Long requesterId = 1L;
        Long familyId = 10L;
        SaveFamilyInviteCommand command = new SaveFamilyInviteCommand(requesterId);

        FamilyMember ownerMember = FamilyMemberFixture.withIdAndRole(1L, familyId, requesterId, FamilyMemberRole.OWNER);

        FamilyInvite savedInvite = FamilyInvite.withId(
            1L,
            familyId,
            requesterId,
            "test-invite-code",
            LocalDateTime.now().plusDays(1),
            10,
            0,
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // Mocking: OWNER인 FamilyMember 조회
        when(findFamilyMemberPort.findByUserIdAndRole(eq(requesterId), eq(FamilyMemberRole.OWNER)))
            .thenReturn(Optional.of(ownerMember));

        // Mocking: 초대 저장 시 저장된 초대 반환
        when(saveFamilyInvitePort.save(any(FamilyInvite.class))).thenReturn(savedInvite);

        // when
        String inviteCode = saveFamilyInviteService.save(command);

        // then
        assertThat(inviteCode).isEqualTo("test-invite-code");
        verify(findFamilyMemberPort).findByUserIdAndRole(eq(requesterId), eq(FamilyMemberRole.OWNER));
        verify(saveFamilyInvitePort).save(any(FamilyInvite.class));
    }

    @Test
    @DisplayName("OWNER가 아닌 사용자가 초대를 생성하면 예외가 발생합니다")
    void save_throws_exception_when_not_owner() {
        // given
        Long requesterId = 1L;
        SaveFamilyInviteCommand command = new SaveFamilyInviteCommand(requesterId);

        // Mocking: OWNER인 FamilyMember 없음
        when(findFamilyMemberPort.findByUserIdAndRole(eq(requesterId), eq(FamilyMemberRole.OWNER)))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> saveFamilyInviteService.save(command))
            .isInstanceOf(FTException.class)
            .satisfies(exception -> {
                FTException ftException = (FTException) exception;
                assertThat(ftException.getCode()).isEqualTo(InviteExceptionCode.NOT_FAMILY_OWNER.getCode());
            });
    }

    @Test
    @DisplayName("command가 null이면 NPE가 발생합니다")
    void save_throws_npe_when_command_is_null() {
        // when & then
        assertThatThrownBy(() -> saveFamilyInviteService.save(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }
}
