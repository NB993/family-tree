package io.jhchoe.familytree.core.family.application.port.in;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import java.util.List;
import java.util.Objects;

/**
 * 멤버 정보와 태그 목록을 함께 담는 DTO입니다.
 *
 * @param member 멤버 정보
 * @param tags   멤버에 할당된 태그 목록
 */
public record FamilyMemberWithTagsInfo(
    FamilyMember member,
    List<FamilyMemberTagMappingInfo.TagSimpleInfo> tags
) {

    public FamilyMemberWithTagsInfo {
        Objects.requireNonNull(member, "member must not be null");
        tags = tags != null ? tags : List.of();
    }

    /**
     * 태그 없이 멤버 정보만으로 생성합니다.
     *
     * @param member 멤버 정보
     * @return FamilyMemberWithTagsInfo 인스턴스
     */
    public static FamilyMemberWithTagsInfo newWithoutTags(final FamilyMember member) {
        return new FamilyMemberWithTagsInfo(member, List.of());
    }
}
