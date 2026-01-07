package io.jhchoe.familytree.core.family.application.port.in;

import java.util.List;

/**
 * 멤버에 할당된 태그 정보를 담는 DTO입니다.
 *
 * @param memberId   멤버 ID
 * @param memberName 멤버 이름
 * @param tags       할당된 태그 목록
 */
public record MemberTagsInfo(
    Long memberId,
    String memberName,
    List<TagSimpleInfo> tags
) {

    /**
     * 태그 간단 정보를 담는 DTO입니다.
     *
     * @param id    태그 ID
     * @param name  태그 이름
     * @param color 태그 색상
     */
    public record TagSimpleInfo(Long id, String name, String color) {
    }

    /**
     * 빈 태그 목록을 가진 MemberTagsInfo를 생성합니다.
     *
     * @param memberId   멤버 ID
     * @param memberName 멤버 이름
     * @return MemberTagsInfo 인스턴스
     */
    public static MemberTagsInfo empty(final Long memberId, final String memberName) {
        return new MemberTagsInfo(memberId, memberName, List.of());
    }
}
