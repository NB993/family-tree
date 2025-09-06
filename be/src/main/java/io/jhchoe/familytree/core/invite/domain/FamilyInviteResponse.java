package io.jhchoe.familytree.core.invite.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * FamilyInviteResponse는 가족 초대에 대한 응답을 나타내는 도메인 엔티티입니다.
 */
public final class FamilyInviteResponse {
    private final Long id;
    private final Long inviteId;
    private final String kakaoId;
    private final String name;
    private final LocalDate birthDate;
    private final LocalDateTime respondedAt;

    private FamilyInviteResponse(
        final Long id,
        final Long inviteId,
        final String kakaoId,
        final String name,
        final LocalDate birthDate,
        final LocalDateTime respondedAt
    ) {
        Objects.requireNonNull(inviteId, "inviteId must not be null");
        Objects.requireNonNull(kakaoId, "kakaoId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("name length must be less than or equal to 100");
        }

        this.id = id;
        this.inviteId = inviteId;
        this.kakaoId = kakaoId;
        this.name = name;
        this.birthDate = birthDate;
        this.respondedAt = respondedAt;
    }

    /**
     * 새로운 초대 응답을 생성합니다.
     *
     * @param inviteId 초대 ID
     * @param kakaoId 카카오 ID
     * @param name 응답자 이름
     * @param birthDate 응답자 생년월일
     * @return 새로 생성된 초대 응답
     */
    public static FamilyInviteResponse newResponse(
        final Long inviteId,
        final String kakaoId,
        final String name,
        final LocalDate birthDate
    ) {
        return new FamilyInviteResponse(
            null,
            inviteId,
            kakaoId,
            name,
            birthDate,
            LocalDateTime.now()
        );
    }

    /**
     * 기존 초대 응답 데이터를 도메인 객체로 복원합니다.
     *
     * @param id 응답 ID
     * @param inviteId 초대 ID
     * @param kakaoId 카카오 ID
     * @param name 응답자 이름
     * @param birthDate 응답자 생년월일
     * @param respondedAt 응답 시간
     * @return 복원된 초대 응답
     */
    public static FamilyInviteResponse withId(
        final Long id,
        final Long inviteId,
        final String kakaoId,
        final String name,
        final LocalDate birthDate,
        final LocalDateTime respondedAt
    ) {
        Objects.requireNonNull(id, "id must not be null");
        
        return new FamilyInviteResponse(
            id,
            inviteId,
            kakaoId,
            name,
            birthDate,
            respondedAt
        );
    }

    public Long getId() {
        return id;
    }

    public Long getInviteId() {
        return inviteId;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }
}