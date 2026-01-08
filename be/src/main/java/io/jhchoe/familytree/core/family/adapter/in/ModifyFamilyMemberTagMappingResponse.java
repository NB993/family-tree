package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.core.family.application.port.in.FamilyMemberTagMappingInfo;
import java.util.List;

/**
 * 멤버 태그 할당 응답 DTO입니다.
 *
 * @param memberId   멤버 ID
 * @param memberName 멤버 이름
 * @param tags       할당된 태그 목록
 */
public record ModifyFamilyMemberTagMappingResponse(
    Long memberId,
    String memberName,
    List<TagResponse> tags
) {
    /**
     * 태그 정보 응답 DTO입니다.
     *
     * @param id    태그 ID
     * @param name  태그 이름
     * @param color 태그 색상
     */
    public record TagResponse(
        Long id,
        String name,
        String color
    ) {
        public static TagResponse from(FamilyMemberTagMappingInfo.TagSimpleInfo tag) {
            return new TagResponse(tag.id(), tag.name(), tag.color());
        }
    }

    /**
     * FamilyMemberTagMappingInfo를 Response로 변환합니다.
     *
     * @param info FamilyMemberTagMappingInfo
     * @return ModifyFamilyMemberTagMappingResponse
     */
    public static ModifyFamilyMemberTagMappingResponse from(FamilyMemberTagMappingInfo info) {
        List<TagResponse> tagResponses = info.tags().stream()
            .map(TagResponse::from)
            .toList();
        return new ModifyFamilyMemberTagMappingResponse(info.memberId(), info.memberName(), tagResponses);
    }
}
