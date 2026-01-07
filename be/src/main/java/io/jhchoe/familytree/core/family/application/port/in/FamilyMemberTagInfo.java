package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import java.time.LocalDateTime;

/**
 * 태그 정보를 담는 DTO입니다.
 *
 * @param id          태그 ID
 * @param name        태그 이름
 * @param color       태그 색상
 * @param memberCount 해당 태그가 할당된 멤버 수
 * @param createdAt   생성 일시
 */
public record FamilyMemberTagInfo(
    Long id,
    String name,
    String color,
    int memberCount,
    LocalDateTime createdAt
) {

    /**
     * 도메인 객체와 멤버 수로부터 FamilyMemberTagInfo를 생성합니다.
     *
     * @param tag         태그 도메인 객체
     * @param memberCount 해당 태그가 할당된 멤버 수
     * @return FamilyMemberTagInfo 인스턴스
     */
    public static FamilyMemberTagInfo fromDomain(FamilyMemberTag tag, int memberCount) {
        return new FamilyMemberTagInfo(
            tag.getId(),
            tag.getName(),
            tag.getColor(),
            memberCount,
            tag.getCreatedAt()
        );
    }
}
