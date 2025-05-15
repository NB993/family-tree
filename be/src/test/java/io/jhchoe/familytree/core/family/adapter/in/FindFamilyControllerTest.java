package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyResponse;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] FindFamilyControllerTest")
class FindFamilyControllerTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        familyJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("가족 정보를 ID로 조회할 수 있다")
    void given_id_when_find_family_by_then_return_family_with_status_200() {
        // given
        Family family = Family.newFamily("Family Name", "descrption", "profileUrl");
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));

        // when & then
        // API 호출
        FindFamilyResponse response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/api/families/{id}", savedFamily.getId())
            .then()
            .statusCode(200)
            .body("id", equalTo(savedFamily.getId().intValue()))
            .body("name", equalTo(savedFamily.getName()))
            .body("description", equalTo(savedFamily.getDescription()))
            .body("profileUrl", equalTo(savedFamily.getProfileUrl()))
            .body("createdBy", equalTo(savedFamily.getCreatedBy().intValue()))
            .body("createdAt", notNullValue())
            .body("modifiedBy", equalTo(savedFamily.getModifiedBy().intValue()))
            .body("modifiedAt", notNullValue())
            .extract()
            .as(FindFamilyResponse.class);

        // then
        // 응답 검증
        assertThat(response.id()).isEqualTo(savedFamily.getId());
        assertThat(response.name()).isEqualTo(savedFamily.getName());
        assertThat(response.description()).isEqualTo(savedFamily.getDescription());
        assertThat(response.profileUrl()).isEqualTo(savedFamily.getProfileUrl());
        assertThat(response.createdBy()).isEqualTo(savedFamily.getCreatedBy());
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.modifiedBy()).isEqualTo(savedFamily.getModifiedBy());
        assertThat(response.modifiedAt()).isNotNull();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 가족 ID로 조회할 경우 404 응답을 반환한다")
    void given_non_existent_id_when_find_family_then_return_status_404() {
        // given
        Long nonExistentId = 999L;

        // when & then
        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/api/families/{id}", nonExistentId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
