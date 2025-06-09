package io.jhchoe.familytree.core.family.application.port.in;

/**
 * 가족명 사용 가능 여부 결과를 나타내는 도메인 객체입니다.
 * 
 * @param available 가족명 사용 가능 여부 (true: 사용 가능, false: 중복으로 사용 불가)
 * @param message 사용자에게 표시할 메시지
 * 
 * @author Claude AI
 * @since 1.0
 */
public record FamilyNameAvailabilityResult(
    Boolean available,
    String message
) {
    
    /**
     * 사용 가능한 가족명인 경우의 결과를 생성합니다.
     * 
     * @return 사용 가능 상태의 결과 객체
     */
    public static FamilyNameAvailabilityResult createAvailable() {
        return new FamilyNameAvailabilityResult(true, "사용 가능한 가족명입니다");
    }
    
    /**
     * 이미 사용 중인 가족명인 경우의 결과를 생성합니다.
     * 
     * @return 사용 불가 상태의 결과 객체
     */
    public static FamilyNameAvailabilityResult createUnavailable() {
        return new FamilyNameAvailabilityResult(false, "이미 사용 중인 가족명입니다");
    }
}
