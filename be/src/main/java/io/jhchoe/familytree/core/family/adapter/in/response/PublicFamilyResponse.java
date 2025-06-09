package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.Family;
import lombok.Getter;

/**
 * 공개 Family 검색 응답을 위한 DTO 클래스입니다.
 * 
 * <p>공개 Family의 기본 정보와 가입 가능 여부를 포함합니다.
 * 민감한 정보는 제외하고 공개해도 안전한 정보만 포함합니다.</p>
 */
@Getter
public class PublicFamilyResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String profileUrl;
    private final Integer memberCount;
    private final boolean canJoin;

    /**
     * 공개 Family 응답 객체를 생성합니다.
     *
     * @param id Family ID
     * @param name 가족명
     * @param description 가족 설명
     * @param profileUrl 프로필 이미지 URL
     * @param memberCount 구성원 수
     * @param canJoin 가입 가능 여부
     */
    public PublicFamilyResponse(
        Long id,
        String name,
        String description,
        String profileUrl,
        Integer memberCount,
        boolean canJoin
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileUrl = profileUrl;
        this.memberCount = memberCount;
        this.canJoin = canJoin;
    }

    /**
     * Family 도메인 객체로부터 PublicFamilyResponse를 생성합니다.
     *
     * @param family Family 도메인 객체
     * @param memberCount 구성원 수
     * @return PublicFamilyResponse 객체
     */
    public static PublicFamilyResponse from(Family family, Integer memberCount) {
        return new PublicFamilyResponse(
            family.getId(),
            family.getName(),
            family.getDescription(),
            family.getProfileUrl(),
            memberCount,
            true // 공개 Family는 기본적으로 가입 가능 (추후 비즈니스 로직에 따라 변경 가능)
        );
    }
}
