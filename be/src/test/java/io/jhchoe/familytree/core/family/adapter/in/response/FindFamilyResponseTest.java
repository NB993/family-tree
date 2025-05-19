package io.jhchoe.familytree.core.family.adapter.in.response;

import static org.assertj.core.api.Assertions.assertThat;

import io.jhchoe.familytree.core.family.domain.Family;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Unit Test] FindFamilyResponseTest")
class FindFamilyResponseTest {

    @Test
    @DisplayName("Family 도메인 객체에서 FindFamilyResponse로 변환할 수 있다")
    void create_response_from_family_domain() {
        // given
        Long id = 1L;
        String name = "테스트 가족";
        String description = "테스트 가족 설명";
        String profileUrl = "http://example.com/profile.jpg";
        Long createdBy = 100L;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        Long modifiedBy = 200L;
        LocalDateTime modifiedAt = LocalDateTime.of(2023, 1, 2, 0, 0);

        Family family = Family.withId(
            id, name, description, profileUrl,
            createdBy, createdAt, modifiedBy, modifiedAt
        );

        // when
        FindFamilyResponse response = FindFamilyResponse.from(family);

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.name()).isEqualTo(name);
        assertThat(response.description()).isEqualTo(description);
        assertThat(response.profileUrl()).isEqualTo(profileUrl);
        assertThat(response.createdBy()).isEqualTo(createdBy);
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.modifiedBy()).isEqualTo(modifiedBy);
        assertThat(response.modifiedAt()).isEqualTo(modifiedAt);
    }

    @Test
    @DisplayName("Family에서 변환된 FindFamilyResponse의 필드가 정확히 매핑된다")
    void field_mapping_is_correct() {
        // given
        Family family = Family.withId(
            2L,
            "다른 가족",
            "다른 설명",
            "http://example.com/other.jpg",
            300L,
            LocalDateTime.of(2023, 2, 1, 0, 0),
            400L,
            LocalDateTime.of(2023, 2, 2, 0, 0)
        );

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
