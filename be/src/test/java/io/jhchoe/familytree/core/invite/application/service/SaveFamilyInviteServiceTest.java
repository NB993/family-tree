package io.jhchoe.familytree.core.invite.application.service;

import io.jhchoe.familytree.core.invite.application.port.in.SaveFamilyInviteCommand;
import io.jhchoe.familytree.core.invite.application.port.out.SaveFamilyInvitePort;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] SaveFamilyInviteServiceTest")
class SaveFamilyInviteServiceTest {

    @InjectMocks
    private SaveFamilyInviteService saveFamilyInviteService;

    @Mock
    private SaveFamilyInvitePort saveFamilyInvitePort;

    @Test
    @DisplayName("초대를 생성하고 초대 코드를 반환합니다")
    void save_creates_invite_and_returns_invite_code() {
        // given
        SaveFamilyInviteCommand command = new SaveFamilyInviteCommand(1L);
        FamilyInvite savedInvite = FamilyInvite.withId(
            1L,
            1L,
            "test-invite-code",
            java.time.LocalDateTime.now().plusDays(1),
            io.jhchoe.familytree.core.invite.domain.FamilyInviteStatus.ACTIVE,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );

        // Mocking: 초대 저장 시 저장된 초대 반환
        when(saveFamilyInvitePort.save(any(FamilyInvite.class))).thenReturn(savedInvite);

        // when
        String inviteCode = saveFamilyInviteService.save(command);

        // then
        assertThat(inviteCode).isEqualTo("test-invite-code");
        verify(saveFamilyInvitePort).save(any(FamilyInvite.class));
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