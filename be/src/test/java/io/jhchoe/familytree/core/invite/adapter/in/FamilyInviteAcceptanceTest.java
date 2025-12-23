package io.jhchoe.familytree.core.invite.adapter.in;

import io.jhchoe.familytree.config.WithMockOAuth2User;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaEntity;
import io.jhchoe.familytree.core.family.adapter.out.persistence.FamilyMemberJpaRepository;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.invite.adapter.out.persistence.FamilyInviteJpaEntity;
import io.jhchoe.familytree.core.invite.adapter.out.persistence.FamilyInviteJpaRepository;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.domain.FamilyInviteStatus;
import io.jhchoe.familytree.docs.AcceptanceTestBase;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * 초대 링크 기능 인수 테스트
 * 
 * 시나리오:
 * 1. 사용자가 로그인한 상태에서 초대 링크를 생성한다
 * 2. 생성된 초대 링크는 프론트엔드 URL과 초대 코드를 포함한다
 * 3. 익명 사용자가 초대 코드로 초대 정보를 조회할 수 있다
 * 4. 사용자는 자신이 생성한 초대 목록을 조회할 수 있다
 * 
 * 플로우:
 * - 초대자: POST /api/invites → 초대 링크 생성 → 카카오톡 등으로 공유
 * - 초대받은 사람: 프론트 URL 클릭 → GET /api/invites/{code} → 초대 정보 확인 → OAuth 인증
 * - 초대자: GET /api/invites/my → 생성한 초대 목록 및 응답 상태 확인
 */
@DisplayName("[Acceptance Test] 초대 링크 API")
class FamilyInviteAcceptanceTest extends AcceptanceTestBase {

    @Autowired
    private FamilyInviteJpaRepository familyInviteJpaRepository;

    @Autowired
    private FamilyMemberJpaRepository familyMemberJpaRepository;

    @AfterEach
    void tearDown() {
        familyInviteJpaRepository.deleteAll();
        familyMemberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("OWNER인 사용자가 초대 링크를 생성할 수 있다")
    @WithMockOAuth2User(id = 1L)
    void authenticated_user_can_create_invite_link() {
        // given - OWNER FamilyMember 생성
        Long familyId = 10L;
        Long userId = 1L;
        FamilyMember ownerMember = FamilyMember.newOwner(
            familyId, userId, null, "소유자", null, null, null
        );
        familyMemberJpaRepository.save(FamilyMemberJpaEntity.from(ownerMember));

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/api/invites")
            .then()
            .status(HttpStatus.OK)
            .body("inviteCode", notNullValue())
            .body("inviteUrl", containsString("/invite/"));

        // then - DB 확인
        assertThat(familyInviteJpaRepository.findAll()).hasSize(1);
        FamilyInviteJpaEntity saved = familyInviteJpaRepository.findAll().get(0);
        assertThat(saved.getFamilyId()).isEqualTo(familyId);
        assertThat(saved.getRequesterId()).isEqualTo(userId);
        assertThat(saved.getStatus()).isEqualTo(FamilyInviteStatus.ACTIVE);
        assertThat(saved.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    @DisplayName("익명 사용자도 초대 코드로 초대 정보를 조회할 수 있다")
    void anonymous_user_can_view_invite_by_code() {
        // given - 초대 링크 생성
        FamilyInvite invite = FamilyInvite.newInvite(10L, 1L, 5);
        FamilyInviteJpaEntity saved = familyInviteJpaRepository.save(
            FamilyInviteJpaEntity.from(invite)
        );
        String inviteCode = saved.getInviteCode();

        // when & then - 익명 사용자가 초대 정보 조회
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/invites/{inviteCode}", inviteCode)
            .then()
            .status(HttpStatus.OK)
            .body("inviteCode", equalTo(inviteCode))
            .body("requesterId", equalTo(1))
            .body("status", equalTo("ACTIVE"))
            .body("expiresAt", notNullValue());
    }

    @Test
    @DisplayName("존재하지 않는 초대 코드로 조회하면 404를 반환한다")
    void returns_404_for_non_existent_invite_code() {
        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/invites/{inviteCode}", "non-existent-code")
            .then()
            .status(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("만료된 초대 코드로 조회하면 400을 반환한다")
    void returns_400_for_expired_invite() {
        // given - 정상 초대 생성 후 저장
        FamilyInvite invite = FamilyInvite.newInvite(10L, 1L, 5);
        FamilyInviteJpaEntity entity = FamilyInviteJpaEntity.from(invite);
        FamilyInviteJpaEntity saved = familyInviteJpaRepository.save(entity);

        // 저장된 엔티티를 다시 조회하여 만료된 초대로 업데이트
        FamilyInvite expiredInvite = FamilyInvite.withId(
            saved.getId(),
            saved.getFamilyId(),
            saved.getRequesterId(),
            saved.getInviteCode(),
            LocalDateTime.now().minusHours(1), // 1시간 전 만료
            10,
            0,
            saved.getStatus(),
            saved.getCreatedAt(),
            saved.getModifiedAt()
        );
        familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(expiredInvite));

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/invites/{inviteCode}", saved.getInviteCode())
            .then()
            .status(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("사용자는 자신이 생성한 초대 목록을 조회할 수 있다")
    @WithMockOAuth2User(id = 1L)
    void user_can_view_their_own_invites() {
        // given - 여러 초대 생성
        FamilyInvite invite1 = FamilyInvite.newInvite(10L, 1L, 5);
        FamilyInvite invite2 = FamilyInvite.newInvite(10L, 1L, 5);
        FamilyInvite otherUserInvite = FamilyInvite.newInvite(10L, 2L, 5);

        familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(invite1));
        familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(invite2));
        familyInviteJpaRepository.save(FamilyInviteJpaEntity.from(otherUserInvite));

        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/invites/my")
            .then()
            .status(HttpStatus.OK)
            .body("$", hasSize(2))
            .body("[0].requesterId", equalTo(1))
            .body("[1].requesterId", equalTo(1));
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 초대 링크를 생성할 수 없다")
    void unauthenticated_user_cannot_create_invite() {
        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/api/invites")
            .then()
            .status(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 초대 목록을 조회할 수 없다")
    void unauthenticated_user_cannot_view_invite_list() {
        // when & then
        RestAssuredMockMvc.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/invites/my")
            .then()
            .status(HttpStatus.UNAUTHORIZED);
    }
}
