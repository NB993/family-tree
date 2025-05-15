package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyResponse;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
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
    void find_family_by_id_success() {
        // given
        // 테스트 데이터 설정
        FamilyEntity savedFamily = familyJpaRepository.save(
            FamilyEntity.builder()
                .name("테스트 가족")
                .description("테스트 가족 설명")
                .profileUrl("http://test-profile.com/image.jpg")
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .modifiedBy(1L)
                .modifiedAt(LocalDateTime.now())
                .build()
        );

        // when
        // API 호출
        FindFamilyResponse response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/api/families/{id}", savedFamily.getId())
            .then()
            .statusCode(HttpStatus.OK.value())
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
    void find_family_by_id_not_found() {
        // given
        // 존재하지 않는 ID
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
