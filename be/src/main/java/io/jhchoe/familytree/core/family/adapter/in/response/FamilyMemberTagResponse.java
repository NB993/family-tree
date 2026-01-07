package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagInfo;
import io.jhchoe.familytree.core.family.domain.FamilyMemberTag;
import java.time.LocalDateTime;

/**
 * 태그 조회 응답 DTO입니다.
 */
public record FamilyMemberTagResponse(
    Long id,
    String name,
    String color,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {

    /**
     * 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param tag 도메인 객체
     * @return 응답 DTO
     */
    public static FamilyMemberTagResponse from(FamilyMemberTag tag) {
        return new FamilyMemberTagResponse(
            tag.getId(),
            tag.getName(),
            tag.getColor(),
            tag.getCreatedAt(),
            tag.getModifiedAt()
        );
    }

    /**
     * FamilyMemberTagInfo로부터 응답 DTO를 생성합니다.
     *
     * @param tagInfo 태그 정보
     * @return 응답 DTO
     */
    public static FamilyMemberTagResponse from(FamilyMemberTagInfo tagInfo) {
        return new FamilyMemberTagResponse(
            tagInfo.id(),
            tagInfo.name(),
            tagInfo.color(),
            tagInfo.createdAt(),
            tagInfo.modifiedAt()
        );
    }
}
