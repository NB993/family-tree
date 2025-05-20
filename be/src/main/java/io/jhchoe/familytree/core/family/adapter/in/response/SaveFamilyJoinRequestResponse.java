package io.jhchoe.familytree.core.family.adapter.in.response;

/**
 * Family 가입 신청 결과를 위한 응답 DTO 클래스입니다.
 */
public record SaveFamilyJoinRequestResponse(
    Long id
) {
    /**
     * 저장된 Family 가입 신청 ID로 응답 객체를 생성합니다.
     *
     * @param id 저장된 가입 신청 ID
     * @return 생성된 응답 객체
     */
    public static SaveFamilyJoinRequestResponse from(Long id) {
        return new SaveFamilyJoinRequestResponse(id);
    }
}
