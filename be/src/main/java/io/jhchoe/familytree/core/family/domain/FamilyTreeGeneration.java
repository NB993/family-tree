package io.jhchoe.familytree.core.family.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 가족트리에서 하나의 세대를 나타내는 객체입니다.
 * 동일한 세대 레벨에 속하는 구성원들을 그룹화합니다.
 */
public record FamilyTreeGeneration(
    int level,
    String displayName,
    List<FamilyTreeNode> members
) {
    /**
     * FamilyTreeGeneration 객체를 생성합니다.
     *
     * @param level 세대 레벨 (0: 조부모, 1: 부모, 2: 자녀)
     * @param displayName 세대명 (화면 표시용)
     * @param members 해당 세대에 속하는 구성원 목록
     */
    public FamilyTreeGeneration {
        if (level < 0) {
            throw new IllegalStateException("level must not be negative");
        }
        Objects.requireNonNull(displayName, "displayName must not be null");
        Objects.requireNonNull(members, "members must not be null");
    }
    
    /**
     * 세대 레벨과 구성원 목록으로 FamilyTreeGeneration을 생성합니다.
     *
     * @param level 세대 레벨
     * @param members 구성원 목록
     * @return 생성된 FamilyTreeGeneration 객체
     */
    public static FamilyTreeGeneration create(int level, List<FamilyTreeNode> members) {
        Objects.requireNonNull(members, "members must not be null");
        
        String displayName = getDisplayNameForLevel(level);
        List<FamilyTreeNode> immutableMembers = Collections.unmodifiableList(members);
        
        return new FamilyTreeGeneration(level, displayName, immutableMembers);
    }
    
    /**
     * 세대 레벨에 따른 표시명을 반환합니다.
     *
     * @param level 세대 레벨
     * @return 세대 표시명
     */
    private static String getDisplayNameForLevel(int level) {
        return switch (level) {
            case 0 -> "조부모";
            case 1 -> "부모";
            case 2 -> "자녀";
            case 3 -> "손자녀";
            case 4 -> "증손자녀";
            default -> level + "세대";
        };
    }
    
    /**
     * 해당 세대에 구성원이 있는지 확인합니다.
     *
     * @return 구성원이 있으면 true, 그렇지 않으면 false
     */
    public boolean hasMembers() {
        return !members.isEmpty();
    }
    
    /**
     * 해당 세대의 구성원 수를 반환합니다.
     *
     * @return 구성원 수
     */
    public int getMemberCount() {
        return members.size();
    }
    
    /**
     * 해당 세대의 활성 구성원만 반환합니다.
     *
     * @return 활성 구성원 목록
     */
    public List<FamilyTreeNode> getActiveMembers() {
        return members.stream()
            .filter(FamilyTreeNode::isActive)
            .toList();
    }
    
    /**
     * 해당 세대의 활성 구성원 수를 반환합니다.
     *
     * @return 활성 구성원 수
     */
    public int getActiveMemberCount() {
        return getActiveMembers().size();
    }
}
