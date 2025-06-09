package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.application.port.in.CheckFamilyNameDuplicationUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FamilyNameAvailabilityResult;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 가족명 중복 확인 서비스입니다.
 * FindFamilyPort를 활용하여 기존 가족명과의 중복 여부를 확인합니다.
 * 
 * @author Claude AI
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckFamilyNameDuplicationService implements CheckFamilyNameDuplicationUseCase {

    private final FindFamilyPort findFamilyPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public FamilyNameAvailabilityResult checkDuplication(String familyName) {
        log.debug("가족명 중복 확인 시작: {}", familyName);
        
        // 가족명 유효성 검증
        validateFamilyName(familyName);
        
        // 정확히 일치하는 가족명 존재 여부 확인
        boolean isDuplicate = findFamilyPort.findByName(familyName).isPresent();
        
        if (isDuplicate) {
            log.debug("가족명 중복 확인 결과: 중복됨 - {}", familyName);
            return FamilyNameAvailabilityResult.createUnavailable();
        } else {
            log.debug("가족명 중복 확인 결과: 사용 가능 - {}", familyName);
            return FamilyNameAvailabilityResult.createAvailable();
        }
    }

    /**
     * 가족명 유효성을 검증합니다.
     * 
     * @param familyName 검증할 가족명
     * @throws IllegalArgumentException 가족명이 null이거나 빈 문자열인 경우
     */
    private void validateFamilyName(String familyName) {
        if (!StringUtils.hasText(familyName)) {
            throw new IllegalArgumentException("가족명은 필수값입니다");
        }
        
        if (familyName.length() > 20) {
            throw new IllegalArgumentException("가족명은 20자를 초과할 수 없습니다");
        }
    }
}
