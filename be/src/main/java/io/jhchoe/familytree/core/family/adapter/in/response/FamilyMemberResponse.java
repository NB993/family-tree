package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.time.LocalDateTime;

/**
 * 구성원 정보 조회 응답 DTO입니다.
 */
public record FamilyMemberResponse(
    Long id,
    Long userId,
    String name,
    String profileUrl,
    LocalDateTime birthday,
    String nationality,
    FamilyMemberStatus status,
    FamilyMemberRole role,
    LocalDateTime createdAt
) {
    
    /**
     * 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param member 도메인 객체
     * @return 응답 DTO
     */
    public static FamilyMemberResponse from(FamilyMember member) {
        return new FamilyMemberResponse(
            member.getId(),
            member.getUserId(),
            member.getName(),
            member.getProfileUrl(),
            member.getBirthday(),
            member.getNationality(),
            member.getStatus(),
            member.getRole(),
            member.getCreatedAt()
        );
    }
}
