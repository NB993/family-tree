package io.jhchoe.familytree.core.family.adapter.in.response;

import static org.assertj.core.api.Assertions.assertThat;

import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FindFamilyResponseTest")
class FindFamilyResponseTest {

    @Test
    @DisplayName("Family 도메인 객체에서 FindFamilyResponse로 변환할 수 있다")
    void create_response_from_family_domain() {
        // given
        Family family = FamilyFixture.withId(1L, "테스트 가족", "테스트 가족 설명", "http://example.com/profile.jpg", true);

        // when
        FindFamilyResponse response = FindFamilyResponse.from(family);

        // then
        assertThat(response.id()).isEqualTo(family.getId());
        assertThat(response.name()).isEqualTo(family.getName());
        assertThat(response.description()).isEqualTo(family.getDescription());
        assertThat(response.profileUrl()).isEqualTo(family.getProfileUrl());
        assertThat(response.createdBy()).isEqualTo(family.getCreatedBy());
        assertThat(response.createdAt()).isEqualTo(family.getCreatedAt());
        assertThat(response.modifiedBy()).isEqualTo(family.getModifiedBy());
        assertThat(response.modifiedAt()).isEqualTo(family.getModifiedAt());
    }

    @Test
    @DisplayName("Family에서 변환된 FindFamilyResponse의 필드가 정확히 매핑된다")
    void field_mapping_is_correct() {
        // given
        Family family = FamilyFixture.withId(2L, "다른 가족", "다른 설명", "http://example.com/other.jpg", true);

        // when
        FindFamilyResponse response = FindFamilyResponse.from(family);

        // then
        assertThat(response.id()).isEqualTo(family.getId());
        assertThat(response.name()).isEqualTo(family.getName());
        assertThat(response.description()).isEqualTo(family.getDescription());
        assertThat(response.profileUrl()).isEqualTo(family.getProfileUrl());
        assertThat(response.createdBy()).isEqualTo(family.getCreatedBy());
        assertThat(response.createdAt()).isEqualTo(family.getCreatedAt());
        assertThat(response.modifiedBy()).isEqualTo(family.getModifiedBy());
        assertThat(response.modifiedAt()).isEqualTo(family.getModifiedAt());
    }
}
