package io.jhchoe.familytree.core.family.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import io.jhchoe.familytree.core.family.domain.FamilyJoinRequest;
import io.jhchoe.familytree.core.family.domain.FamilyJoinRequestStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FamilyJoinRequestJpaEntityTest")
class FamilyJoinRequestJpaEntityTest {

    @Test
    @DisplayName("도메인 객체를 JPA 엔티티로 변환할 수 있다")
    void convert_domain_to_jpa_entity() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        Long requesterId = 3L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.PENDING;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        Long createdBy = 3L;
        LocalDateTime modifiedAt = LocalDateTime.of(2023, 1, 2, 0, 0);
        Long modifiedBy = 3L;

        FamilyJoinRequest domain = FamilyJoinRequest.withId(
            id, familyId, requesterId, status, createdAt, createdBy, modifiedAt, modifiedBy
        );

        // when
        FamilyJoinRequestJpaEntity entity = FamilyJoinRequestJpaEntity.from(domain);

        // then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getFamilyId()).isEqualTo(familyId);
        assertThat(entity.getRequesterId()).isEqualTo(requesterId);
        assertThat(entity.getStatus()).isEqualTo(status);
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.getCreatedBy()).isEqualTo(createdBy);
        assertThat(entity.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(entity.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(entity.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("JPA 엔티티를 도메인 객체로 변환할 수 있다")
    void convert_jpa_entity_to_domain() {
        // given
        Long id = 1L;
        Long familyId = 2L;
        Long requesterId = 3L;
        FamilyJoinRequestStatus status = FamilyJoinRequestStatus.APPROVED;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        Long createdBy = 3L;
        LocalDateTime modifiedAt = LocalDateTime.of(2023, 1, 2, 0, 0);
        Long modifiedBy = 3L;

        FamilyJoinRequestJpaEntity entity = new FamilyJoinRequestJpaEntity();
        entity.setId(id);
        entity.setFamilyId(familyId);
        entity.setRequesterId(requesterId);
        entity.setStatus(status);
        entity.setDeleted(false);
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(createdAt);
        entity.setModifiedBy(modifiedBy);
        entity.setModifiedAt(modifiedAt);

        // when
        FamilyJoinRequest domain = entity.toFamilyJoinRequest();

        // then
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getFamilyId()).isEqualTo(familyId);
        assertThat(domain.getRequesterId()).isEqualTo(requesterId);
        assertThat(domain.getStatus()).isEqualTo(status);
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
        assertThat(domain.getCreatedBy()).isEqualTo(createdBy);
        assertThat(domain.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(domain.getModifiedBy()).isEqualTo(modifiedBy);
    }

    @Test
    @DisplayName("생성된 엔티티의 deleted 필드는 false로 설정된다")
    void deleted_field_is_false_by_default() {
        // given
        FamilyJoinRequest domain = FamilyJoinRequest.newRequest(1L, 2L);

        // when
        FamilyJoinRequestJpaEntity entity = FamilyJoinRequestJpaEntity.from(domain);

        // then
        assertThat(entity.isDeleted()).isFalse();
    }
}
