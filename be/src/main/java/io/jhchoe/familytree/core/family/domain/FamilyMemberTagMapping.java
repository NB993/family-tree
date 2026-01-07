package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * FamilyMemberTagMapping 클래스는 FamilyMember와 FamilyMemberTag 간의 다대다 매핑을 표현합니다.
 * <p>
 * 한 멤버에 여러 태그, 한 태그에 여러 멤버가 연결될 수 있습니다.
 */
@Getter
public final class FamilyMemberTagMapping {

    private final Long id;
    private final Long tagId;
    private final Long memberId;
    private final LocalDateTime createdAt;

    /**
     * FamilyMemberTagMapping 생성자 (private).
     *
     * @param id        고유 ID
     * @param tagId     태그 ID
     * @param memberId  멤버 ID
     * @param createdAt 생성 일시
     */
    private FamilyMemberTagMapping(
        final Long id,
        final Long tagId,
        final Long memberId,
        final LocalDateTime createdAt
    ) {
        this.id = id;
        this.tagId = tagId;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }

    /**
     * 새로운 태그 매핑을 생성합니다.
     *
     * @param tagId    태그 ID
     * @param memberId 멤버 ID
     * @return 새로운 FamilyMemberTagMapping 인스턴스 (ID 없음)
     * @throws NullPointerException tagId 또는 memberId가 null인 경우
     */
    public static FamilyMemberTagMapping newMapping(final Long tagId, final Long memberId) {
        Objects.requireNonNull(tagId, "tagId는 null일 수 없습니다");
        Objects.requireNonNull(memberId, "memberId는 null일 수 없습니다");

        return new FamilyMemberTagMapping(null, tagId, memberId, LocalDateTime.now());
    }

    /**
     * 기존 태그 매핑을 복원합니다.
     * DB에서 조회한 데이터를 도메인 객체로 복원할 때 사용합니다.
     *
     * @param id        고유 ID
     * @param tagId     태그 ID
     * @param memberId  멤버 ID
     * @param createdAt 생성 일시
     * @return FamilyMemberTagMapping 인스턴스
     * @throws NullPointerException 필수 파라미터가 null인 경우
     */
    public static FamilyMemberTagMapping withId(
        final Long id,
        final Long tagId,
        final Long memberId,
        final LocalDateTime createdAt
    ) {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");
        Objects.requireNonNull(tagId, "tagId는 null일 수 없습니다");
        Objects.requireNonNull(memberId, "memberId는 null일 수 없습니다");
        Objects.requireNonNull(createdAt, "createdAt은 null일 수 없습니다");

        return new FamilyMemberTagMapping(id, tagId, memberId, createdAt);
    }
}
