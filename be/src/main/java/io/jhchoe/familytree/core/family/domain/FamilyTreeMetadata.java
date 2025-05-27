package io.jhchoe.familytree.core.family.domain;

import java.util.List;
import java.util.Objects;

/**
 * 가족트리의 메타데이터를 나타내는 객체입니다.
 * 트리의 전체적인 통계 및 정보를 포함합니다.
 */
public record FamilyTreeMetadata(
    int totalMembers,
    int activeMembers,
    int maxGenerations,
    int generationCount,
    boolean hasCompleteTree
) {
    /**
     * FamilyTreeMetadata 객체를 생성합니다.
     *
     * @param totalMembers 전체 구성원 수
     * @param activeMembers 활성 구성원 수
     * @param maxGenerations 최대 세대 수
     * @param generationCount 실제 존재하는 세대 수
     * @param hasCompleteTree 완전한 트리인지 여부
     */
    public FamilyTreeMetadata {
        if (totalMembers < 0) {
            throw new IllegalArgumentException("totalMembers must not be negative");
        }
        if (activeMembers < 0) {
            throw new IllegalArgumentException("activeMembers must not be negative");
        }
        if (maxGenerations < 0) {
            throw new IllegalArgumentException("maxGenerations must not be negative");
        }
        if (generationCount < 0) {
            throw new IllegalArgumentException("generationCount must not be negative");
        }
        if (activeMembers > totalMembers) {
            throw new IllegalArgumentException("activeMembers cannot exceed totalMembers");
        }
    }
    
    /**
     * 구성원 목록과 세대 정보로부터 메타데이터를 생성합니다.
     *
     * @param allMembers 모든 구성원 목록
     * @param generations 세대별 정보
     * @return 생성된 메타데이터
     */
    public static FamilyTreeMetadata create(
        List<FamilyTreeNode> allMembers,
        List<FamilyTreeGeneration> generations
    ) {
        Objects.requireNonNull(allMembers, "allMembers must not be null");
        Objects.requireNonNull(generations, "generations must not be null");
        
        int totalMembers = allMembers.size();
        int activeMembers = (int) allMembers.stream().filter(FamilyTreeNode::isActive).count();
        int generationCount = generations.size();
        int maxGenerations = generations.stream()
            .mapToInt(FamilyTreeGeneration::level)
            .max()
            .orElse(0) + 1; // 0-based이므로 1을 더함
        
        // 완전한 트리인지 판단 (모든 세대에 최소 1명의 활성 구성원이 있는 경우)
        boolean hasCompleteTree = generations.stream()
            .allMatch(generation -> generation.getActiveMemberCount() > 0);
        
        return new FamilyTreeMetadata(
            totalMembers,
            activeMembers,
            maxGenerations,
            generationCount,
            hasCompleteTree
        );
    }
    
    /**
     * 빈 메타데이터를 생성합니다.
     *
     * @return 빈 트리를 나타내는 메타데이터
     */
    public static FamilyTreeMetadata empty() {
        return new FamilyTreeMetadata(0, 0, 0, 0, false);
    }
    
    /**
     * 트리가 비어있는지 확인합니다.
     *
     * @return 구성원이 없으면 true, 그렇지 않으면 false
     */
    public boolean isEmpty() {
        return totalMembers == 0;
    }
    
    /**
     * 모든 구성원이 활성 상태인지 확인합니다.
     *
     * @return 모든 구성원이 활성 상태이면 true, 그렇지 않으면 false
     */
    public boolean areAllMembersActive() {
        return totalMembers > 0 && totalMembers == activeMembers;
    }
    
    /**
     * 비활성 구성원 수를 반환합니다.
     *
     * @return 비활성 구성원 수
     */
    public int getInactiveMembers() {
        return totalMembers - activeMembers;
    }
    
    /**
     * 활성 구성원 비율을 반환합니다.
     *
     * @return 활성 구성원 비율 (0.0 ~ 1.0)
     */
    public double getActiveMemberRatio() {
        return totalMembers == 0 ? 0.0 : (double) activeMembers / totalMembers;
    }
}
