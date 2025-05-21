package io.jhchoe.familytree.core.family.adapter.in.response;

import lombok.Getter;

/**
 * 구성원 역할 변경 응답 DTO입니다.
 */
@Getter
public class UpdateFamilyMemberRoleResponse {
    
    private final Long id;
    
    /**
     * 응답 객체를 생성합니다.
     *
     * @param id 변경된 구성원 ID
     */
    public UpdateFamilyMemberRoleResponse(Long id) {
        this.id = id;
    }
}
