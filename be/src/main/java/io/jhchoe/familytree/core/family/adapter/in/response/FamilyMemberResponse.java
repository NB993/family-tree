package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 구성원 정보 조회 응답 DTO입니다.
 */
@Getter
public class FamilyMemberResponse {
    
    private final Long id;
    private final Long userId;
    private final String name;
    private final String profileUrl;
    private final LocalDateTime birthday;
    private final String nationality;
    private final FamilyMemberStatus status;
    private final FamilyMemberRole role;
    private final LocalDateTime createdAt;
    
    /**
     * 응답 객체를 생성합니다.
     *
     * @param id          ID
     * @param userId      사용자 ID
     * @param name        이름
     * @param profileUrl  프로필 URL
     * @param birthday    생일
     * @param nationality 국적
     * @param status      상태
     * @param role        역할
     * @param createdAt   가입 일시
     */
    private FamilyMemberResponse(
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
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.profileUrl = profileUrl;
        this.birthday = birthday;
        this.nationality = nationality;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
    }
    
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
