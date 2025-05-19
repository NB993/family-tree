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

        // JPA 엔티티를 도메인 객체로 변환하여 테스트 설정
        FamilyJoinRequest domain = FamilyJoinRequest.withId(
            id, familyId, requesterId, status, createdAt, createdBy, modifiedAt, modifiedBy
        );
        FamilyJoinRequestJpaEntity entity = FamilyJoinRequestJpaEntity.from(domain);

        // when
        FamilyJoinRequest result = entity.toFamilyJoinRequest();

        // then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFamilyId()).isEqualTo(familyId);
        assertThat(result.getRequesterId()).isEqualTo(requesterId);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getCreatedBy()).isEqualTo(createdBy);
        assertThat(result.getModifiedAt()).isEqualTo(modifiedAt);
        assertThat(result.getModifiedBy()).isEqualTo(modifiedBy);
    }

    @Test
    @DisplayName("새로운 가입 신청을 JPA 엔티티로 변환할 수 있다")
    void convert_new_request_to_jpa_entity() {
        // given
        Long familyId = 1L;
        Long requesterId = 2L;
        FamilyJoinRequest newRequest = FamilyJoinRequest.newRequest(familyId, requesterId);

        // when
        FamilyJoinRequestJpaEntity entity = FamilyJoinRequestJpaEntity.from(newRequest);

        // then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getFamilyId()).isEqualTo(familyId);
        assertThat(entity.getRequesterId()).isEqualTo(requesterId);
        assertThat(entity.getStatus()).isEqualTo(FamilyJoinRequestStatus.PENDING);
        assertThat(entity.isDeleted()).isFalse();
    }
}
