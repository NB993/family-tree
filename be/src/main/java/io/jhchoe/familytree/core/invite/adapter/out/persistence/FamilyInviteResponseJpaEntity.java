package io.jhchoe.familytree.core.invite.adapter.out.persistence;

import io.jhchoe.familytree.core.invite.domain.FamilyInviteResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * FamilyInviteResponse 엔티티를 DB에 저장하기 위한 JPA 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "family_invite_response", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"invite_id", "kakao_id"}))
@Entity
public class FamilyInviteResponseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invite_id", nullable = false)
    private Long inviteId;

    @Column(name = "kakao_id", nullable = false)
    private String kakaoId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "responded_at", nullable = false)
    private LocalDateTime respondedAt;

    /**
     * FamilyInviteResponseJpaEntity 객체를 생성하는 생성자입니다.
     */
    public FamilyInviteResponseJpaEntity(
        final Long id,
        final Long inviteId,
        final String kakaoId,
        final String name,
        final LocalDate birthDate,
        final LocalDateTime respondedAt
    ) {
        this.id = id;
        this.inviteId = inviteId;
        this.kakaoId = kakaoId;
        this.name = name;
        this.birthDate = birthDate;
        this.respondedAt = respondedAt;
    }

    /**
     * FamilyInviteResponse 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param response 도메인 객체
     * @return JPA 엔티티
     */
    public static FamilyInviteResponseJpaEntity from(final FamilyInviteResponse response) {
        Objects.requireNonNull(response, "response must not be null");

        return new FamilyInviteResponseJpaEntity(
            response.getId(),
            response.getInviteId(),
            response.getKakaoId(),
            response.getName(),
            response.getBirthDate(),
            response.getRespondedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 도메인 객체
     */
    public FamilyInviteResponse toFamilyInviteResponse() {
        return FamilyInviteResponse.withId(
            id,
            inviteId,
            kakaoId,
            name,
            birthDate,
            respondedAt
        );
    }
}