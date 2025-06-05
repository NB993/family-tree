package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.application.port.in.FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Family 구성원 조회 서비스입니다.
 * 기존 구현된 FindFamilyMemberPort를 활용하여 구성원을 조회하고 정렬합니다.
 */
@Service
@Transactional(readOnly = true)
public class FindFamilyMemberService implements FindFamilyMemberUseCase {

    private final FindFamilyMemberPort findFamilyMemberPort;

    /**
     * FindFamilyMemberService 생성자입니다.
     *
     * @param findFamilyMemberPort Family 구성원 조회 포트
     */
    public FindFamilyMemberService(FindFamilyMemberPort findFamilyMemberPort) {
        this.findFamilyMemberPort = findFamilyMemberPort;
    }

    /**
     * 특정 Family 구성원을 조회합니다.
     * 권한에 따른 필터링이 적용됩니다.
     *
     * @param query 단건 조회 조건을 담은 쿼리 객체
     * @return 조회된 Family 구성원
     * @throws IllegalArgumentException query가 null이거나 유효하지 않은 경우
     */
    @Override
    public FamilyMember find(FindFamilyMemberByIdQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }

        // 현재 사용자가 Family 구성원인지 확인
        FamilyMember currentMember = getCurrentMember(query.getFamilyId(), query.getCurrentUserId());

        // 대상 구성원 조회
        Optional<FamilyMember> targetMember = findFamilyMemberPort.findById(query.getTargetMemberId());
        if (targetMember.isEmpty()) {
            throw new IllegalArgumentException("Target member not found");
        }

        // 권한 확인
        if (!shouldIncludeMember(targetMember.get(), currentMember)) {
            throw new IllegalArgumentException("Access denied to target member");
        }

        return targetMember.get();
    }

    /**
     * Family 구성원 목록을 조회합니다.
     * 기존 FindFamilyMemberPort의 findAllByFamilyId()를 활용하여 조회합니다.
     *
     * @param query 복수 조회 조건을 담은 쿼리 객체
     * @return 나이순으로 정렬된 Family 구성원 목록
     * @throws IllegalArgumentException query가 null이거나 사용자가 Family 구성원이 아닌 경우
     */
    @Override
    public List<FamilyMember> findAll(FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }

        // 현재 사용자가 Family 구성원인지 확인
        FamilyMember currentMember = getCurrentMember(query.getFamilyId(), query.getCurrentUserId());

        // 전체 구성원 조회 (기존 구현 활용)
        List<FamilyMember> allMembers = findFamilyMemberPort.findAllByFamilyId(query.getFamilyId());

        // 권한별 필터링 및 정렬
        return filterAndSortMembers(allMembers, currentMember);
    }

    /**
     * 현재 사용자 정보를 조회합니다.
     *
     * @param familyId Family ID
     * @param currentUserId 현재 사용자 ID
     * @return 현재 사용자 정보
     * @throws IllegalArgumentException 사용자가 Family 구성원이 아닌 경우
     */
    private FamilyMember getCurrentMember(Long familyId, Long currentUserId) {
        Optional<FamilyMember> currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(familyId, currentUserId);
        
        if (currentMember.isEmpty()) {
            throw new IllegalArgumentException("User is not a member of this family");
        }

        return currentMember.get();
    }

    /**
     * 권한별 필터링 및 나이순 정렬을 수행합니다.
     *
     * @param allMembers 전체 구성원 목록
     * @param currentMember 현재 사용자 정보
     * @return 필터링 및 정렬된 구성원 목록
     */
    private List<FamilyMember> filterAndSortMembers(List<FamilyMember> allMembers, FamilyMember currentMember) {
        return allMembers.stream()
            .filter(member -> shouldIncludeMember(member, currentMember))
            .sorted(createAgeComparator())
            .collect(Collectors.toList());
    }

    /**
     * 구성원을 결과에 포함할지 결정합니다.
     * ADMIN 이상은 SUSPENDED 포함 모든 구성원, 일반 사용자는 ACTIVE만 조회 가능합니다.
     *
     * @param member 확인할 구성원
     * @param currentMember 현재 사용자
     * @return 포함 여부
     */
    private boolean shouldIncludeMember(FamilyMember member, FamilyMember currentMember) {
        // ADMIN 이상은 SUSPENDED도 볼 수 있음
        if (isAdminOrAbove(currentMember.getRole())) {
            return true;
        }
        
        // 일반 사용자는 ACTIVE 상태만 볼 수 있음
        return member.getStatus() == FamilyMemberStatus.ACTIVE;
    }

    /**
     * ADMIN 이상 권한인지 확인합니다.
     *
     * @param role 확인할 권한
     * @return ADMIN 이상 권한 여부
     */
    private boolean isAdminOrAbove(FamilyMemberRole role) {
        return role == FamilyMemberRole.ADMIN || role == FamilyMemberRole.OWNER;
    }

    /**
     * 나이순(어린 순서) 정렬을 위한 Comparator를 생성합니다.
     * 생일 정보가 없는 구성원은 맨 뒤에 배치됩니다.
     *
     * @return 나이순 정렬 Comparator
     */
    private Comparator<FamilyMember> createAgeComparator() {
        return (member1, member2) -> {
            LocalDateTime birthday1 = member1.getBirthday();
            LocalDateTime birthday2 = member2.getBirthday();

            // 생일 정보가 없는 경우 맨 뒤로
            if (birthday1 == null && birthday2 == null) {
                return 0;
            }
            if (birthday1 == null) {
                return 1; // member1을 뒤로
            }
            if (birthday2 == null) {
                return -1; // member2를 뒤로
            }

            // 생일이 늦을수록 어리므로 앞에 배치 (내림차순)
            return birthday2.compareTo(birthday1);
        };
    }
}
