package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 가족 구성원 목록을 다양한 API 응답으로 변환하는 일급 객체입니다.
 * 도메인 지식을 캡슐화하고 API별 맞춤 응답을 제공합니다.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>Family 홈 API용 데이터 변환</li>
 *   <li>권한별 필터링 지원</li>
 *   <li>향후 확장 가능한 변환 메서드 제공</li>
 * </ul>
 */
public class FamilyMembersWithRelationshipsResponse {

    private final List<FamilyMember> members;

    /**
     * FamilyMembersWithRelationshipsResponse 생성자입니다.
     *
     * @param members 가족 구성원 목록 (null 불허)
     */
    public FamilyMembersWithRelationshipsResponse(List<FamilyMember> members) {
        Objects.requireNonNull(members, "members must not be null");

        // 방어적 복사로 불변성 보장
        this.members = List.copyOf(members);
    }

    /**
     * Family 홈 API 응답용 데이터 구조로 변환합니다.
     *
     * <p>변환 규칙:</p>
     * <ul>
     *   <li>ACTIVE 상태 구성원만 포함</li>
     *   <li>나이순(어린 순서) 정렬</li>
     * </ul>
     *
     * @return Family 홈 API 응답 리스트
     */
    public List<FamilyMemberWithRelationshipResponse> toMemberWithRelationships() {
        return toMemberWithRelationships(Collections.emptyMap());
    }

    /**
     * Family 홈 API 응답용 데이터 구조로 변환합니다. (태그 정보 포함)
     *
     * <p>변환 규칙:</p>
     * <ul>
     *   <li>ACTIVE 상태 구성원만 포함</li>
     *   <li>나이순(어린 순서) 정렬</li>
     *   <li>태그 정보가 없는 경우 빈 리스트 처리</li>
     * </ul>
     *
     * @param memberTagsMap 멤버 ID별 태그 목록 맵
     * @return Family 홈 API 응답 리스트
     */
    public List<FamilyMemberWithRelationshipResponse> toMemberWithRelationships(
        Map<Long, List<FamilyMemberWithRelationshipResponse.TagInfo>> memberTagsMap
    ) {
        Map<Long, List<FamilyMemberWithRelationshipResponse.TagInfo>> tagsMap =
            memberTagsMap != null ? memberTagsMap : Collections.emptyMap();

        return members.stream()
            .filter(member -> member.getStatus() == FamilyMemberStatus.ACTIVE)
            .map(member -> {
                List<FamilyMemberWithRelationshipResponse.TagInfo> tags =
                    tagsMap.getOrDefault(member.getId(), Collections.emptyList());
                return new FamilyMemberWithRelationshipResponse(member, tags);
            })
            .sorted(createAgeComparator()) // 나이순 정렬
            .toList();
    }

    /**
     * ACTIVE 상태 구성원 목록을 반환합니다.
     *
     * @return ACTIVE 상태 구성원 목록
     */
    public List<FamilyMember> getActiveMembers() {
        return members.stream()
            .filter(member -> member.getStatus() == FamilyMemberStatus.ACTIVE)
            .toList();
    }

    /**
     * 나이순(어린 순서) 정렬을 위한 Comparator를 생성합니다.
     * 생일 정보가 없는 구성원은 맨 뒤에 배치됩니다.
     *
     * @return 나이순 정렬 Comparator
     */
    private Comparator<FamilyMemberWithRelationshipResponse> createAgeComparator() {
        return (response1, response2) -> {
            FamilyMember member1 = response1.getMember();
            FamilyMember member2 = response2.getMember();

            if (member1.getBirthday() == null && member2.getBirthday() == null) {
                return 0;
            }
            if (member1.getBirthday() == null) {
                return 1; // member1을 뒤로
            }
            if (member2.getBirthday() == null) {
                return -1; // member2를 뒤로
            }

            // 생일이 늦을수록 어리므로 앞에 배치 (내림차순)
            return member2.getBirthday().compareTo(member1.getBirthday());
        };
    }
}
