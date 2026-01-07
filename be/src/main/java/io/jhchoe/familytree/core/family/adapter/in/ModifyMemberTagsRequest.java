package io.jhchoe.familytree.core.family.adapter.in;

import java.util.List;

/**
 * 멤버 태그 할당 요청 DTO입니다.
 *
 * @param tagIds 할당할 태그 ID 목록
 */
public record ModifyMemberTagsRequest(
    List<Long> tagIds
) {
    public ModifyMemberTagsRequest {
        if (tagIds == null) {
            tagIds = List.of();
        }
    }
}
