package io.jhchoe.familytree.core.family.adapter.in.response;

import lombok.Getter;

/**
 * 구성원 상태 변경 응답 DTO입니다.
 */
@Getter
public class UpdateFamilyMemberStatusResponse {
    
    private final Long id;
    
    /**
     * 응답 객체를 생성합니다.
     *
     * @param id 변경된 구성원 ID
     */
    public UpdateFamilyMemberStatusResponse(Long id) {
        this.id = id;
    }
}
