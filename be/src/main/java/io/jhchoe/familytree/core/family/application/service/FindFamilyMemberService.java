package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberByIdQuery;
import io.jhchoe.familytree.core.family.application.port.in.FindFamilyMemberUseCase;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.validation.FamilyValidationService;
import io.jhchoe.familytree.core.family.domain.FamilyMember;
import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import io.jhchoe.familytree.core.family.exception.FamilyExceptionCode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    private final FamilyValidationService familyValidationService;

    /**
     * FindFamilyMemberService 생성자입니다.
     *
     * @param findFamilyMemberPort Family 구성원 조회 포트
     * @param familyValidationService Family 검증 서비스
     */
    public FindFamilyMemberService(FindFamilyMemberPort findFamilyMemberPort, FamilyValidationService familyValidationService) {
        this.findFamilyMemberPort = findFamilyMemberPort;
        this.familyValidationService = familyValidationService;
    }

    /**
     * 특정 Family 구성원을 조회합니다.
     * 권한에 따른 필터링이 적용됩니다.
     *
     * @param query 단건 조회 조건을 담은 쿼리 객체
     * @return 조회된 Family 구성원
     * @throws NullPointerException query가 null인 경우 (개발자 실수)
     * @throws FTException 권한이 없거나 구성원을 찾을 수 없는 경우
     */
    @Override
    public FamilyMember find(FindFamilyMemberByIdQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        // 1. Family 존재 여부 검증
        familyValidationService.validateFamilyExists(query.getFamilyId());
        
        // 2. 구성원 조회하면서 동시에 현재 사용자 권한 검증
        FamilyMember currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        // 3. 대상 구성원 조회
        FamilyMember targetMember = findFamilyMemberPort.findById(query.getTargetMemberId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.MEMBER_NOT_FOUND));

        // 4. 권한 확인
        if (!shouldIncludeMember(targetMember, currentMember)) {
            throw new FTException(FamilyExceptionCode.NOT_AUTHORIZED);
        }

        return targetMember;
    }

    /**
     * Family 구성원 목록을 조회합니다.
     * 기존 FindFamilyMemberPort의 findAllByFamilyId()를 활용하여 조회합니다.
     *
     * @param query 복수 조회 조건을 담은 쿼리 객체
     * @return 나이순으로 정렬된 Family 구성원 목록
     * @throws NullPointerException query가 null인 경우 (개발자 실수)
     * @throws FTException Family가 존재하지 않거나 사용자가 Family 구성원이 아닌 경우
     */
    @Override
    public List<FamilyMember> findAll(FindActiveFamilyMembersByFamilyIdAndCurrentUserQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        // 1. Family 존재 여부 검증
        familyValidationService.validateFamilyExists(query.getFamilyId());
        
        // 2. 구성원 조회하면서 동시에 권한 검증
        FamilyMember currentMember = findFamilyMemberPort
            .findByFamilyIdAndUserId(query.getFamilyId(), query.getCurrentUserId())
            .orElseThrow(() -> new FTException(FamilyExceptionCode.NOT_FAMILY_MEMBER));

        // 3. 전체 구성원 조회 (기존 구현 활용)
        List<FamilyMember> allMembers = findFamilyMemberPort.findAllByFamilyId(query.getFamilyId());

        // 4. 권한별 필터링 및 정렬
        return filterAndSortMembers(allMembers, currentMember);
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
