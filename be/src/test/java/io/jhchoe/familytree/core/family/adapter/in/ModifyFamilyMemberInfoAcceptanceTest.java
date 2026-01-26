package io.jhchoe.familytree.core.family.adapter.in;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyJpaRepository;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.BirthdayType;
import io.jhchoe.familytree.core.family.domain.Family;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.jhchoe.familytree.test.fixture.FamilyFixture;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@DisplayName("[Acceptance Test] ModifyFamilyMemberInfoController")
class ModifyFamilyMemberInfoAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyJpaRepository familyJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    void tearDown() {
        familyMemberJpaRepository.deleteAll();
        familyJpaRepository.deleteAll();
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 수동 등록된 구성원의 전체 정보(이름, 생일) 수정 시 200 OK를 반환합니다")
    void modify_returns_200_when_owner_modifies_manually_registered_member_info() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long targetMemberId = createManuallyRegisteredMember(familyId, "홍길동", FamilyMemberRole.MEMBER);

        String requestBody = """
            {
                "name": "김철수",
                "birthday": "1985-03-15T00:00:00",
                "birthdayType": "LUNAR"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/info", familyId, targetMemberId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(targetMemberId.intValue()));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 초대된 구성원의 이름만 수정 시 200 OK를 반환합니다")
    void modify_returns_200_when_owner_modifies_invited_member_name_only() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long targetMemberId = createMember(familyId, 2L, "홍길동", FamilyMemberRole.MEMBER);

        String requestBody = """
            {
                "name": "김철수"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/info", familyId, targetMemberId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(targetMemberId.intValue()));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("OWNER가 초대된 구성원의 생일 수정 시도 시 403 Forbidden을 반환합니다")
    void modify_returns_403_when_owner_tries_to_modify_invited_member_birthday() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long targetMemberId = createMember(familyId, 2L, "홍길동", FamilyMemberRole.MEMBER);

        String requestBody = """
            {
                "name": "김철수",
                "birthday": "1985-03-15T00:00:00",
                "birthdayType": "LUNAR"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/info", familyId, targetMemberId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("ADMIN이 구성원 정보 수정 시 200 OK를 반환합니다")
    void modify_returns_200_when_admin_modifies_member_info() {
        // given
        Long currentUserId = 1L;
        Long ownerUserId = 100L;
        Long familyId = createFamilyWithOwner(ownerUserId);
        createMember(familyId, currentUserId, "관리자", FamilyMemberRole.ADMIN);
        Long targetMemberId = createMember(familyId, 3L, "홍길동", FamilyMemberRole.MEMBER);

        String requestBody = """
            {
                "name": "김철수"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/info", familyId, targetMemberId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(targetMemberId.intValue()));
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("일반 구성원이 다른 구성원 정보 수정 시 403 Forbidden을 반환합니다")
    void modify_returns_403_when_member_tries_to_modify() {
        // given
        Long currentUserId = 1L;
        Long ownerUserId = 100L;
        Long familyId = createFamilyWithOwner(ownerUserId);
        createMember(familyId, currentUserId, "일반구성원", FamilyMemberRole.MEMBER);
        Long targetMemberId = createMember(familyId, 3L, "홍길동", FamilyMemberRole.MEMBER);

        String requestBody = """
            {
                "name": "김철수"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/info", familyId, targetMemberId)
        .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("본인 정보 수정 시도 시 400 Bad Request를 반환합니다")
    void modify_returns_400_when_trying_to_modify_self() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        FamilyMemberJpaEntity ownerMember = familyMemberJpaRepository.findByFamilyIdAndUserId(familyId, userId).orElseThrow();

        String requestBody = """
            {
                "name": "새이름"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/info", familyId, ownerMember.getId())
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("존재하지 않는 구성원 정보 수정 시 404 Not Found를 반환합니다")
    void modify_returns_404_when_member_not_found() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long nonExistentMemberId = 999L;

        String requestBody = """
            {
                "name": "김철수"
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/info", familyId, nonExistentMemberId)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithMockOAuth2User
    @DisplayName("이름이 빈 문자열인 경우 400 Bad Request를 반환합니다")
    void modify_returns_400_when_name_is_blank() {
        // given
        Long userId = 1L;
        Long familyId = createFamilyWithOwner(userId);
        Long targetMemberId = createMember(familyId, 2L, "홍길동", FamilyMemberRole.MEMBER);

        String requestBody = """
            {
                "name": "   "
            }
            """;

        // when & then
        given()
            .postProcessors(csrf())
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/api/families/{familyId}/members/{memberId}/info", familyId, targetMemberId)
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private Long createFamilyWithOwner(Long ownerUserId) {
        Family family = FamilyFixture.newFamily();
        FamilyJpaEntity savedFamily = familyJpaRepository.save(FamilyJpaEntity.from(family));
        Long familyId = savedFamily.getId();

        LocalDateTime now = LocalDateTime.now();
        FamilyMember ownerMember = FamilyMember.withRole(
            familyId, ownerUserId, "Owner", "owner.jpg",
            now.minusYears(30), BirthdayType.SOLAR, FamilyMemberRole.OWNER
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        return familyId;
    }

    private Long createMember(Long familyId, Long userId, String name, FamilyMemberRole role) {
        LocalDateTime now = LocalDateTime.now();
        FamilyMember member = FamilyMember.withRole(
            familyId, userId, name, "profile.jpg",
            now.minusYears(25), BirthdayType.SOLAR, role
        );
        FamilyMemberJpaEntity savedEntity = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));
        return savedEntity.getId();
    }

    /**
     * 수동 등록 멤버 생성 (userId = null).
     * OWNER가 직접 등록한 멤버로, 애완동물이나 아이 등을 나타냅니다.
     */
    private Long createManuallyRegisteredMember(Long familyId, String name, FamilyMemberRole role) {
        LocalDateTime now = LocalDateTime.now();
        FamilyMember member = FamilyMember.newManualMember(
            familyId, name, null, null,
            now.minusYears(25), BirthdayType.SOLAR
        );
        FamilyMemberJpaEntity savedEntity = familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(member));
        return savedEntity.getId();
    }
}
