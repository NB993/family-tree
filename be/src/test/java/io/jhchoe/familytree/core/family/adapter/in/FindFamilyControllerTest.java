package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.in.response.FindFamilyResponse;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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

    @Test
    @WithMockOAuth2User
    @DisplayName("name 으로 Family 정보를 조회할 경우 입력한 name 문자열을 포함하는 모든 Family 목록을 반환한다")
    void given_name_when_find_families_then_return_families_with_status_200() {
        // given
        Family family1 = Family.newFamily("Family Name1", "description1", "profileUrl1");
        Family family2 = Family.newFamily("Family Name2", "description2", "profileUrl2");
        FamilyJpaEntity savedFamily1 = familyJpaRepository.save(FamilyJpaEntity.from(family1));
        FamilyJpaEntity savedFamily2 = familyJpaRepository.save(FamilyJpaEntity.from(family2));

        // when & then
        List<FindFamilyResponse> response = given()
            .contentType(MediaType.APPLICATION_JSON)
            .queryParam("name", "Family")
            .when()
            .get("/api/families")
            .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("id", containsInAnyOrder(savedFamily1.getId().intValue(), savedFamily2.getId().intValue()))
            .body("name", containsInAnyOrder("Family Name1", "Family Name2"))
            // 배열의 설명들이 저장된 설명들과 일치하는지 확인
            .body("description", containsInAnyOrder("description1", "description2"))
            // 배열의 프로필 URL들이 저장된 프로필 URL들과 일치하는지 확인
            .body("profileUrl", containsInAnyOrder("profileUrl1", "profileUrl2"))
            // 생성자와 생성일자, 수정자와 수정일자가 모두 존재하는지 확인
            .body("createdBy", everyItem(notNullValue()))
            .body("createdAt", everyItem(notNullValue()))
            .body("modifiedBy", everyItem(notNullValue()))
            .body("modifiedAt", everyItem(notNullValue()))
            .extract()
            .as(new TypeRef<List<FindFamilyResponse>>() {}); // 쓸 일은 없는데 추후 참고용으로 놔둠
    }
}
