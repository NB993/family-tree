package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] SaveFamilyMemberTagController")
class SaveFamilyMemberTagAcceptanceTest extends AcceptanceTestBase {

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

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 태그 생성 시 201 Created를 반환합니다")
    void save_returns_201_when_owner_creates_tag() {
        // given
        Long userId = 1L;
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
        Long userId = 1L;
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
        Long userId = 1L;
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
