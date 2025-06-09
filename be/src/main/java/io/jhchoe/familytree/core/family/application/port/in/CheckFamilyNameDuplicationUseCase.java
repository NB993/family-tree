package io.jhchoe.familytree.core.family.application.port.in;

/**
 * 가족명 중복 여부를 확인하는 UseCase 인터페이스입니다.
 * 
 * @author Claude AI
 * @since 1.0
 */
public interface CheckFamilyNameDuplicationUseCase {
    
    /**
     * 가족명 중복 여부를 확인합니다.
     * 
     * @param familyName 확인할 가족명 (null 또는 빈 문자열 불가)
     * @return 가족명 사용 가능 여부 정보
     * @throws IllegalArgumentException 가족명이 null이거나 빈 문자열인 경우
     */
    FamilyNameAvailabilityResult checkDuplication(String familyName);
}
