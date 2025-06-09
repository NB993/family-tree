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
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.shared.valueobject.Nationality;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.time.LocalDate;
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

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("가족 정보를 ID로 조회할 수 있다")
    void given_id_when_find_family_by_then_return_family_with_status_200() {
        // given
        Family family = Family.newFamily("Family Name", "descrption", "profileUrl", true);
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
        Family family1 = Family.newFamily("Family Name1", "description1", "profileUrl1", true);
        Family family2 = Family.newFamily("Family Name2", "description2", "profileUrl2", true);
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

    @Test
    @WithMockOAuth2User(id = 100L)
    @DisplayName("내 소속 Family 목록을 조회할 수 있다")
    void given_authenticated_user_when_find_my_families_then_return_my_families_with_status_200() {
        // given
        Long currentUserId = 100L;
        
        // Family 생성
        Family family1 = Family.newFamily("우리가족", "행복한 우리 가족", "profile1.jpg", false);
        Family family2 = Family.newFamily("친척가족", "큰집 가족", "profile2.jpg", true);
        Family family3 = Family.newFamily("다른가족", "관련없는 가족", "profile3.jpg", false);
        
        FamilyJpaEntity savedFamily1 = familyJpaRepository.save(FamilyJpaEntity.from(family1));
        FamilyJpaEntity savedFamily2 = familyJpaRepository.save(FamilyJpaEntity.from(family2));
        FamilyJpaEntity savedFamily3 = familyJpaRepository.save(FamilyJpaEntity.from(family3));
        
        // 현재 사용자를 family1, family2의 구성원으로 추가
        FamilyMember member1 = FamilyMember.withRole(
            savedFamily1.getId(), currentUserId, "홍길동", "profile.jpg", 
            LocalDate.of(1990, 1, 1).atStartOfDay(), "Korean", 
            FamilyMemberRole.OWNER
        );
        FamilyMember member2 = FamilyMember.withRole(
            savedFamily2.getId(), currentUserId, "홍길동", "profile.jpg",
            LocalDate.of(1990, 1, 1).atStartOfDay(), "Korean",
            FamilyMemberRole.MEMBER
        );
        
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member1));
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member2));

        // when & then
        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/api/families/my")
            .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("id", containsInAnyOrder(savedFamily1.getId().intValue(), savedFamily2.getId().intValue()))
            .body("name", containsInAnyOrder("우리가족", "친척가족"))
            .body("description", containsInAnyOrder("행복한 우리 가족", "큰집 가족"))
            .body("profileUrl", containsInAnyOrder("profile1.jpg", "profile2.jpg"));
    }

    @Test
    @WithMockOAuth2User(id = 200L)
    @DisplayName("소속된 Family가 없는 경우 빈 목록을 반환한다")
    void given_user_with_no_families_when_find_my_families_then_return_empty_list() {
        // given
        // 다른 사용자의 Family는 있지만 현재 사용자(200L)는 소속되지 않음
        Family family = Family.newFamily("다른가족", "관련없는 가족", "profile.jpg", false);
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        
        FamilyMember member = FamilyMember.withRole(
            savedFamily.getId(), 999L, "다른사람", "other.jpg",
            LocalDate.of(1990, 1, 1).atStartOfDay(), "Korean",
            FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));

        // when & then
        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/api/families/my")
            .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }
}
