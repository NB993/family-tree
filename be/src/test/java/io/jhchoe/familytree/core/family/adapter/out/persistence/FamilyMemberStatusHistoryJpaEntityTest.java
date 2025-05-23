package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatusHistory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyMemberStatusHistoryJpaEntityTest")
class FamilyMemberStatusHistoryJpaEntityTest {

    @Test
    @DisplayName("from 메서드는 유효한 FamilyMemberStatusHistory 객체를 입력받아 JpaEntity 객체를 정상적으로 생성해야 한다")
    void given_valid_status_history_when_from_then_return_jpa_entity() {
        // given
        Long id = 1L;
        Long familyId = 101L;
        Long memberId = 202L;
        FamilyMemberStatus status = FamilyMemberStatus.SUSPENDED;
        String reason = "규칙 위반";
        Long createdBy = 1001L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        FamilyMemberStatusHistory statusHistory = FamilyMemberStatusHistory.withId(
            id, familyId, memberId, status, reason, createdBy, createdAt
        );

        // when
        FamilyMemberStatusHistoryJpaEntity result = FamilyMemberStatusHistoryJpaEntity.from(statusHistory);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getReason()).isEqualTo(reason);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("from 메서드는 null FamilyMemberStatusHistory를 입력받는 경우 NullPointerException을 발생시켜야 한다")
    void given_null_status_history_when_from_then_throw_exception() {
        // given
        FamilyMemberStatusHistory statusHistory = null;

        // when & then
        assertThatThrownBy(() -> FamilyMemberStatusHistoryJpaEntity.from(statusHistory))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("statusHistory must not be null");
    }

    @Test
    @DisplayName("toDomainEntity 메서드는 JpaEntity를 도메인 객체로 올바르게 변환해야 한다")
    void given_jpa_entity_when_to_domain_entity_then_return_domain_object() {
        // given
        Long id = 1L;
        Long familyId = 101L;
        Long memberId = 202L;
        FamilyMemberStatus status = FamilyMemberStatus.BANNED;
        String reason = "스팸 행위";
        Long createdBy = 1001L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        FamilyMemberStatusHistoryJpaEntity entity = new FamilyMemberStatusHistoryJpaEntity(
            id, familyId, memberId, status, reason, createdBy, createdAt
        );

        // when
        FamilyMemberStatusHistory result = entity.toDomainEntity();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getReason()).isEqualTo(reason);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("새로운 상태 이력 생성을 위한 from 메서드는 ID가 null인 객체를 올바르게 처리해야 한다")
    void given_new_status_history_when_from_then_return_entity_without_id() {
        // given
        Long familyId = 101L;
        Long memberId = 202L;
        FamilyMemberStatus status = FamilyMemberStatus.ACTIVE;
        String reason = "상태 복구";

        FamilyMemberStatusHistory statusHistory = FamilyMemberStatusHistory.create(
            familyId, memberId, status, reason
        );

        // when
        FamilyMemberStatusHistoryJpaEntity result = FamilyMemberStatusHistoryJpaEntity.from(statusHistory);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull(); // 새로운 엔티티이므로 ID는 null
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getReason()).isEqualTo(reason);
        assertThat(result.getCreatedBy()).isNull(); // 새로운 엔티티이므로 audit 필드는 null
        assertThat(result.getCreatedAt()).isNull();
    }
}
