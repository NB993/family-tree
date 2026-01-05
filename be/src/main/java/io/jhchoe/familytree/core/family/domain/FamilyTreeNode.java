package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 가족트리 시각화를 위한 트리 노드를 나타내는 도메인 객체입니다.
 * 프론트엔드 렌더링에 최적화된 구조로 설계되었습니다.
 */
public record FamilyTreeNode(
    Long memberId,
    String name,
    Integer age,
    String profileUrl,
    Long userId,
    FamilyMemberRole role,
    FamilyMemberStatus status,
    boolean isMe,
    int generation,
    List<FamilyTreeRelation> relations
) {
    /**
     * FamilyTreeNode 객체를 생성합니다.
     *
     * @param memberId 가족 구성원 ID
     * @param name 이름
     * @param age 나이 (생일 기준 계산된 값)
     * @param profileUrl 프로필 이미지 URL
     * @param userId 연결된 사용자 ID
     * @param role 가족 내 역할
     * @param status 구성원 상태
     * @param isMe 현재 사용자 본인 여부
     * @param generation 세대 레벨 (0: 조부모, 1: 부모, 2: 자녀)
     * @param relations 다른 구성원과의 관계 목록
     */
    public FamilyTreeNode {
        Objects.requireNonNull(memberId, "memberId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(relations, "relations must not be null");

        if (generation < 0) {
            throw new IllegalArgumentException("generation must not be negative");
        }
    }
    
    /**
     * FamilyMember 도메인 객체로부터 FamilyTreeNode를 생성합니다.
     *
     * @param member 가족 구성원 도메인 객체
     * @param currentUserId 현재 로그인한 사용자 ID
     * @param generation 세대 레벨
     * @param relations 관계 목록
     * @return 생성된 FamilyTreeNode 객체
     */
    public static FamilyTreeNode from(
        FamilyMember member,
        Long currentUserId,
        int generation,
        List<FamilyTreeRelation> relations
    ) {
        Objects.requireNonNull(member, "member must not be null");
        Objects.requireNonNull(relations, "relations must not be null");
        
        Integer age = calculateAge(member.getBirthday());
        boolean isMe = currentUserId != null && currentUserId.equals(member.getUserId());
        
        return new FamilyTreeNode(
            member.getId(),
            member.getName(),
            age,
            member.getProfileUrl(),
            member.getUserId(),
            member.getRole(),
            member.getStatus(),
            isMe,
            generation,
            relations
        );
    }
    
    /**
     * 생일로부터 나이를 계산합니다.
     *
     * @param birthday 생일
     * @return 계산된 나이, 생일이 null인 경우 null 반환
     */
    private static Integer calculateAge(LocalDateTime birthday) {
        if (birthday == null) {
            return null;
        }
        
        LocalDateTime now = LocalDateTime.now();
        int age = now.getYear() - birthday.getYear();
        
        // 아직 생일이 지나지 않은 경우 1살 차감
        if (now.getMonthValue() < birthday.getMonthValue() ||
            (now.getMonthValue() == birthday.getMonthValue() && now.getDayOfMonth() < birthday.getDayOfMonth())) {
            age--;
        }
        
        return Math.max(0, age);
    }
    
    /**
     * 구성원이 활성 상태인지 확인합니다.
     *
     * @return 활성 상태이면 true, 그렇지 않으면 false
     */
    public boolean isActive() {
        return status == FamilyMemberStatus.ACTIVE;
    }
    
    /**
     * 지정된 역할 이상의 권한을 가지고 있는지 확인합니다.
     *
     * @param requiredRole 필요한 최소 역할
     * @return 요구되는 역할 이상의 권한을 가지고 있으면 true, 그렇지 않으면 false
     */
    public boolean hasRoleAtLeast(FamilyMemberRole requiredRole) {
        return role.isAtLeast(requiredRole);
    }
}
