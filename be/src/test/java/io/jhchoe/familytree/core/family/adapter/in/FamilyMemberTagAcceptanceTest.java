package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberTagJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberTagJpaRepository;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] FamilyMemberTagController")
class FamilyMemberTagAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @Autowired
    private FamilyMemberTagJpaRepository familyMemberTagJpaRepository;

    @AfterEach
    void tearDown() {
        familyMemberTagJpaRepository.deleteAll();
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/families/{familyId}/tags - 태그 생성")
    class SaveTagTest {

        @Test
        @WithMockOAuth2User
        @DisplayName("OWNER가 태그 생성 시 201 Created를 반환합니다")
        void save_returns_201_when_owner_creates_tag() {
            // given
            Long userId = 1L; // WithMockOAuth2User 기본 사용자 ID
            Long familyId = createFamilyWithOwner(userId);

            String requestBody = """
                {
                    "name": "친가 어른들"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("OWNER가 아닌 구성원이 태그 생성 시 403 Forbidden을 반환합니다")
        void save_returns_403_when_non_owner_creates_tag() {
            // given
            Long userId = 1L; // WithMockOAuth2User 기본 사용자 ID
            Long ownerUserId = 100L;
            Long familyId = createFamilyWithOwnerAndMember(ownerUserId, userId);

            String requestBody = """
                {
                    "name": "친가 어른들"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("구성원이 아닌 사용자가 태그 생성 시 403 Forbidden을 반환합니다")
        void save_returns_403_when_non_member_creates_tag() {
            // given
            Long userId = 1L; // WithMockOAuth2User 기본 사용자 ID
            Long ownerUserId = 100L;
            Long familyId = createFamilyWithOwner(ownerUserId);

            String requestBody = """
                {
                    "name": "친가 어른들"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("태그가 10개일 때 추가 생성 시 400 Bad Request를 반환합니다")
        void save_returns_400_when_tag_limit_exceeded() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);

            // 10개 태그 생성
            for (int i = 1; i <= 10; i++) {
                createTag(familyId, "태그" + i, userId);
            }

            String requestBody = """
                {
                    "name": "11번째태그"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("중복된 태그 이름으로 생성 시 400 Bad Request를 반환합니다")
        void save_returns_400_when_name_duplicated() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);
            createTag(familyId, "친가", userId);

            String requestBody = """
                {
                    "name": "친가"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("인증되지 않은 사용자가 태그 생성 시 401 Unauthorized를 반환합니다")
        void save_returns_401_when_unauthenticated() {
            // given
            Long familyId = 1L;

            String requestBody = """
                {
                    "name": "친가"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Nested
    @DisplayName("GET /api/families/{familyId}/tags - 태그 목록 조회")
    class FindTagsTest {

        @Test
        @WithMockOAuth2User
        @DisplayName("구성원이 태그 목록 조회 시 200 OK와 태그 목록을 반환합니다")
        void find_returns_200_and_tags_when_member_requests() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);
            createTag(familyId, "친가", userId);
            createTag(familyId, "외가", userId);
            createTag(familyId, "조카들", userId);

            // when & then
            given()
            .when()
                .get("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(3));
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("태그가 없으면 빈 목록을 반환합니다")
        void find_returns_empty_list_when_no_tags() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);

            // when & then
            given()
            .when()
                .get("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("구성원이 아닌 사용자가 조회 시 403 Forbidden을 반환합니다")
        void find_returns_403_when_non_member_requests() {
            // given
            Long userId = 1L;
            Long ownerUserId = 100L;
            Long familyId = createFamilyWithOwner(ownerUserId);

            // when & then
            given()
            .when()
                .get("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("태그 목록은 가나다순으로 정렬됩니다")
        void find_returns_tags_sorted_by_name() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);
            createTag(familyId, "조카들", userId);
            createTag(familyId, "가족", userId);
            createTag(familyId, "어른들", userId);

            // when & then
            given()
            .when()
                .get("/api/families/{familyId}/tags", familyId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(3))
                .body("[0].name", equalTo("가족"))
                .body("[1].name", equalTo("어른들"))
                .body("[2].name", equalTo("조카들"));
        }
    }

    @Nested
    @DisplayName("PUT /api/families/{familyId}/tags/{tagId} - 태그 수정")
    class ModifyTagTest {

        @Test
        @WithMockOAuth2User
        @DisplayName("OWNER가 태그 수정 시 200 OK와 수정된 태그 정보를 반환합니다")
        void modify_returns_200_when_owner_modifies_tag() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);
            Long tagId = createTag(familyId, "친가", userId);

            String requestBody = """
                {
                    "name": "외가"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .put("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(tagId.intValue()))
                .body("name", equalTo("외가"));
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("OWNER가 태그 색상 수정 시 200 OK를 반환합니다")
        void modify_returns_200_when_owner_modifies_color() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);
            Long tagId = createTag(familyId, "친가", userId);

            String requestBody = """
                {
                    "name": "친가",
                    "color": "#D3E5EF"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .put("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("친가"))
                .body("color", equalTo("#D3E5EF"));
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("OWNER가 아닌 구성원이 태그 수정 시 403 Forbidden을 반환합니다")
        void modify_returns_403_when_non_owner_modifies_tag() {
            // given
            Long userId = 1L;
            Long ownerUserId = 100L;
            Long familyId = createFamilyWithOwnerAndMember(ownerUserId, userId);
            Long tagId = createTag(familyId, "친가", ownerUserId);

            String requestBody = """
                {
                    "name": "외가"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .put("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
            .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("존재하지 않는 태그 수정 시 404 Not Found를 반환합니다")
        void modify_returns_404_when_tag_not_found() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);
            Long nonExistentTagId = 999L;

            String requestBody = """
                {
                    "name": "외가"
                }
                """;

            // when & then
            given()
                .postProcessors(csrf())
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .put("/api/families/{familyId}/tags/{tagId}", familyId, nonExistentTagId)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("DELETE /api/families/{familyId}/tags/{tagId} - 태그 삭제")
    class DeleteTagTest {

        @Test
        @WithMockOAuth2User
        @DisplayName("OWNER가 태그 삭제 시 204 No Content를 반환합니다")
        void delete_returns_204_when_owner_deletes_tag() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);
            Long tagId = createTag(familyId, "친가", userId);

            // when & then
            given()
                .postProcessors(csrf())
            .when()
                .delete("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("OWNER가 아닌 구성원이 태그 삭제 시 403 Forbidden을 반환합니다")
        void delete_returns_403_when_non_owner_deletes_tag() {
            // given
            Long userId = 1L;
            Long ownerUserId = 100L;
            Long familyId = createFamilyWithOwnerAndMember(ownerUserId, userId);
            Long tagId = createTag(familyId, "친가", ownerUserId);

            // when & then
            given()
                .postProcessors(csrf())
            .when()
                .delete("/api/families/{familyId}/tags/{tagId}", familyId, tagId)
            .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @WithMockOAuth2User
        @DisplayName("존재하지 않는 태그 삭제 시 404 Not Found를 반환합니다")
        void delete_returns_404_when_tag_not_found() {
            // given
            Long userId = 1L;
            Long familyId = createFamilyWithOwner(userId);
            Long nonExistentTagId = 999L;

            // when & then
            given()
                .postProcessors(csrf())
            .when()
                .delete("/api/families/{familyId}/tags/{tagId}", familyId, nonExistentTagId)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // 헬퍼 메서드들

    private Long createFamilyWithOwner(Long ownerUserId) {
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        LocalDateTime now = LocalDateTime.now();
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "Owner", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        return familyId;
    }

    private Long createFamilyWithOwnerAndMember(Long ownerUserId, Long memberUserId) {
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        LocalDateTime now = LocalDateTime.now();

        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "Owner", "owner.jpg",
            now.minusYears(30), null, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        FamilyMember normalMember = FamilyMember.newMember(
            familyId, memberUserId, "Member", "member.jpg",
            now.minusYears(25), null
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(normalMember));

        return familyId;
    }

    private Long createTag(Long familyId, String name, Long createdBy) {
        FamilyMemberTag tag = FamilyMemberTag.newTag(familyId, name, createdBy);
        FamilyMemberTagJpaEntity entity = FamilyMemberTagJpaEntity.from(tag);
        FamilyMemberTagJpaEntity savedEntity = familyMemberTagJpaRepository.save(entity);
        return savedEntity.getId();
    }
}
