package io.jhchoe.familytree.core.invite.adapter.out.persistence;

import io.jhchoe.familytree.common.support.ModifierBaseEntity;
import io.jhchoe.familytree.core.invite.domain.FamilyInvite;
import io.jhchoe.familytree.core.invite.domain.FamilyInviteStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * FamilyInvite 엔티티를 DB에 저장하기 위한 JPA 엔티티 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "family_invite")
@Entity
public class FamilyInviteJpaEntity extends ModifierBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "invite_code", nullable = false, unique = true)
    private String inviteCode;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count", nullable = false)
    private Integer usedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FamilyInviteStatus status;

    /**
     * FamilyInviteJpaEntity 객체를 생성하는 생성자입니다.
     */
    private FamilyInviteJpaEntity(
        final Long id,
        final Long requesterId,
        final String inviteCode,
        final LocalDateTime expiresAt,
        final Integer maxUses,
        final Integer usedCount,
        final FamilyInviteStatus status,
        final Long createdBy,
        final LocalDateTime createdAt,
        final Long modifiedBy,
        final LocalDateTime modifiedAt
    ) {
        super(createdBy, createdAt, modifiedBy, modifiedAt);
        this.id = id;
        this.requesterId = requesterId;
        this.inviteCode = inviteCode;
        this.expiresAt = expiresAt;
        this.maxUses = maxUses;
        this.usedCount = usedCount;
        this.status = status;
    }

    /**
     * FamilyInvite 도메인 객체로부터 JPA 엔티티를 생성합니다.
     *
     * @param familyInvite 도메인 객체
     * @return JPA 엔티티
     */
    public static FamilyInviteJpaEntity from(final FamilyInvite familyInvite) {
        Objects.requireNonNull(familyInvite, "familyInvite must not be null");

        return new FamilyInviteJpaEntity(
            familyInvite.getId(),
            familyInvite.getRequesterId(),
            familyInvite.getInviteCode(),
            familyInvite.getExpiresAt(),
            familyInvite.getMaxUses(),
            familyInvite.getUsedCount(),
            familyInvite.getStatus(),
            familyInvite.getRequesterId(), // createdBy는 requesterId와 동일
            familyInvite.getCreatedAt(),
            familyInvite.getRequesterId(), // modifiedBy도 requesterId로 초기화
            familyInvite.getModifiedAt()
        );
    }

    /**
     * JPA 엔티티를 도메인 객체로 변환합니다.
     *
     * @return 도메인 객체
     */
    public FamilyInvite toFamilyInvite() {
        return FamilyInvite.withId(
            id,
            requesterId,
            inviteCode,
            expiresAt,
            maxUses,
            usedCount,
            status,
            getCreatedAt(),
            getModifiedAt()
        );
    }
}