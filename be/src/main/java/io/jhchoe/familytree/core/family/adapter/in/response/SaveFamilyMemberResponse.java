package io.jhchoe.familytree.core.family.adapter.in.response;

/**
 * 가족 구성원 수동 등록 응답 DTO입니다.
 *
 * @param id 저장된 구성원 ID
 */
public record SaveFamilyMemberResponse(Long id) {

    /**
     * 저장된 구성원 ID로 응답 객체를 생성합니다.
     *
     * @param id 저장된 구성원 ID
     * @return SaveFamilyMemberResponse
     */
    public static SaveFamilyMemberResponse of(Long id) {
        return new SaveFamilyMemberResponse(id);
    }
}
