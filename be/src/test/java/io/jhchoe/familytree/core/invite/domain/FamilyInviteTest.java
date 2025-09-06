package io.jhchoe.familytree.core.invite.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] FamilyInviteTest")
class FamilyInviteTest {

    @Test
    @DisplayName("새로운 초대를 생성하면 활성 상태와 24시간 만료 시간을 가집니다")
    void newInvite_creates_active_invite_with_24hour_expiry() {
        // when
        FamilyInvite invite = FamilyInvite.newInvite(1L);

        // then
        assertThat(invite.getId()).isNull();
        assertThat(invite.getRequesterId()).isEqualTo(1L);
        assertThat(invite.getInviteCode()).isNotNull();
        assertThat(invite.getStatus()).isEqualTo(FamilyInviteStatus.ACTIVE);
        assertThat(invite.getExpiresAt()).isAfter(LocalDateTime.now());
        assertThat(invite.getExpiresAt()).isBefore(LocalDateTime.now().plusDays(1).plusMinutes(1));
    }

    @Test
    @DisplayName("기존 데이터로 초대를 복원할 수 있습니다")
    void withId_restores_existing_invite() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(1);

        // when
        FamilyInvite invite = FamilyInvite.withId(
            1L,
            2L,
            "test-code",
            expiresAt,
            FamilyInviteStatus.ACTIVE,
            now,
            now
        );

        // then
        assertThat(invite.getId()).isEqualTo(1L);
        assertThat(invite.getRequesterId()).isEqualTo(2L);
        assertThat(invite.getInviteCode()).isEqualTo("test-code");
        assertThat(invite.getStatus()).isEqualTo(FamilyInviteStatus.ACTIVE);
        assertThat(invite.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("ID가 null이면 예외가 발생합니다")
    void withId_throws_exception_when_id_is_null() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> FamilyInvite.withId(
            null,
            1L,
            "test-code",
            now.plusDays(1),
            FamilyInviteStatus.ACTIVE,
            now,
            now
        )).isInstanceOf(NullPointerException.class)
          .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("활성 상태이고 만료되지 않은 초대는 활성 상태입니다")
    void isActive_returns_true_when_active_and_not_expired() {
        // given
        FamilyInvite invite = FamilyInvite.withId(
            1L,
            1L,
            "test-code",
            LocalDateTime.now().plusHours(1),
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // when & then
        assertThat(invite.isActive()).isTrue();
    }

    @Test
    @DisplayName("만료 시간이 지난 초대는 활성 상태가 아닙니다")
    void isActive_returns_false_when_expired() {
        // given
        FamilyInvite invite = FamilyInvite.withId(
            1L,
            1L,
            "test-code",
            LocalDateTime.now().minusHours(1),
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );

        // when & then
        assertThat(invite.isActive()).isFalse();
        assertThat(invite.isExpired()).isTrue();
    }

    @Test
    @DisplayName("초대를 완료 상태로 변경할 수 있습니다")
    void complete_changes_status_to_completed() {
        // given
        FamilyInvite invite = FamilyInvite.newInvite(1L);

        // when
        FamilyInvite completedInvite = invite.complete();

        // then
        assertThat(completedInvite.getStatus()).isEqualTo(FamilyInviteStatus.COMPLETED);
        assertThat(completedInvite.getModifiedAt()).isAfter(invite.getModifiedAt());
    }

    @Test
    @DisplayName("초대를 만료 상태로 변경할 수 있습니다")
    void expire_changes_status_to_expired() {
        // given
        FamilyInvite invite = FamilyInvite.newInvite(1L);

        // when
        FamilyInvite expiredInvite = invite.expire();

        // then
        assertThat(expiredInvite.getStatus()).isEqualTo(FamilyInviteStatus.EXPIRED);
        assertThat(expiredInvite.getModifiedAt()).isAfter(invite.getModifiedAt());
    }
}