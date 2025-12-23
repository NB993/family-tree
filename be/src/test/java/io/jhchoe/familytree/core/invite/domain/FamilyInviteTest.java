package io.jhchoe.familytree.core.invite.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Unit Test] FamilyInviteTest")
class FamilyInviteTest {

    @Test
    @DisplayName("새로운 초대를 생성하면 활성 상태와 1일 만료 시간을 가집니다")
    void newInvite_creates_active_invite_with_1day_expiry() {
        // given
        Long familyId = 10L;
        Integer maxUses = 10;

        // when
        FamilyInvite invite = FamilyInvite.newInvite(familyId, 1L, maxUses);

        // then
        assertThat(invite.getId()).isNull();
        assertThat(invite.getFamilyId()).isEqualTo(familyId);
        assertThat(invite.getRequesterId()).isEqualTo(1L);
        assertThat(invite.getInviteCode()).isNotNull();
        assertThat(invite.getStatus()).isEqualTo(FamilyInviteStatus.ACTIVE);
        assertThat(invite.getMaxUses()).isEqualTo(maxUses);
        assertThat(invite.getUsedCount()).isEqualTo(0);
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
            10L, // familyId
            2L,
            "test-code",
            expiresAt,
            10, // maxUses
            3,  // usedCount
            FamilyInviteStatus.ACTIVE,
            now,
            now
        );

        // then
        assertThat(invite.getId()).isEqualTo(1L);
        assertThat(invite.getFamilyId()).isEqualTo(10L);
        assertThat(invite.getRequesterId()).isEqualTo(2L);
        assertThat(invite.getInviteCode()).isEqualTo("test-code");
        assertThat(invite.getStatus()).isEqualTo(FamilyInviteStatus.ACTIVE);
        assertThat(invite.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(invite.getMaxUses()).isEqualTo(10);
        assertThat(invite.getUsedCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("ID가 null이면 예외가 발생합니다")
    void withId_throws_exception_when_id_is_null() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> FamilyInvite.withId(
            null,
            10L, // familyId
            1L,
            "test-code",
            now.plusDays(1),
            null,
            0,
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
            10L, // familyId
            1L,
            "test-code",
            LocalDateTime.now().plusHours(1),
            null,
            0,
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
            10L, // familyId
            1L,
            "test-code",
            LocalDateTime.now().minusHours(1),
            null,
            0,
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
        FamilyInvite invite = FamilyInvite.newInvite(10L, 1L, null);

        // when
        FamilyInvite completedInvite = invite.complete();

        // then
        assertThat(completedInvite.getStatus()).isEqualTo(FamilyInviteStatus.COMPLETED);
    }

    @Test
    @DisplayName("초대를 만료 상태로 변경할 수 있습니다")
    void expire_changes_status_to_expired() {
        // given
        FamilyInvite invite = FamilyInvite.newInvite(10L, 1L, null);

        // when
        FamilyInvite expiredInvite = invite.expire();

        // then
        assertThat(expiredInvite.getStatus()).isEqualTo(FamilyInviteStatus.EXPIRED);
    }
    
    @Test
    @DisplayName("incrementUsedCount 메서드는 사용 횟수를 1 증가시킵니다")
    void increment_used_count_increases_count_by_one() {
        // given
        FamilyInvite invite = FamilyInvite.withId(
            1L,
            10L, // familyId
            2L,
            "test-code",
            LocalDateTime.now().plusDays(1),
            10, // maxUses
            3,  // usedCount
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // when
        FamilyInvite updatedInvite = invite.incrementUsedCount();

        // then
        assertThat(updatedInvite.getUsedCount()).isEqualTo(4);
        assertThat(updatedInvite.getMaxUses()).isEqualTo(10);
        assertThat(updatedInvite.getStatus()).isEqualTo(FamilyInviteStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("무제한 초대 링크는 maxUses가 null입니다")
    void unlimited_invite_has_null_max_uses() {
        // when
        FamilyInvite invite = FamilyInvite.newInvite(10L, 1L, null);

        // then
        assertThat(invite.getMaxUses()).isNull();
        assertThat(invite.getUsedCount()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("초대 링크가 최대 사용 횟수에 도달했는지 확인할 수 있습니다")
    void can_check_if_max_uses_reached() {
        // given
        FamilyInvite almostFullInvite = FamilyInvite.withId(
            1L, 10L, 2L, "test-code",
            LocalDateTime.now().plusDays(1),
            5, // maxUses
            4, // usedCount
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        FamilyInvite fullInvite = FamilyInvite.withId(
            2L, 10L, 2L, "test-code2",
            LocalDateTime.now().plusDays(1),
            5, // maxUses
            5, // usedCount
            FamilyInviteStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // then
        assertThat(almostFullInvite.getUsedCount() < almostFullInvite.getMaxUses()).isTrue();
        assertThat(fullInvite.getUsedCount() >= fullInvite.getMaxUses()).isTrue();
    }
}