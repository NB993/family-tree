package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.application.port.in.FamilyNameAvailabilityResult;

/**
 * 가족명 사용 가능 여부 응답 DTO입니다.
 * 
 * @param available 가족명 사용 가능 여부 (true: 사용 가능, false: 중복으로 사용 불가)
 * @param message 사용자에게 표시할 메시지
 * 
 * @author Claude AI
 * @since 1.0
 */
public record FamilyNameAvailabilityResponse(
    Boolean available,
    String message
) {
    
    /**
     * 도메인 객체로부터 응답 DTO를 생성합니다.
     * 
     * @param result 가족명 사용 가능 여부 결과
     * @return 응답 DTO
     */
    public static FamilyNameAvailabilityResponse from(FamilyNameAvailabilityResult result) {
        return new FamilyNameAvailabilityResponse(
            result.available(),
            result.message()
        );
    }
}
