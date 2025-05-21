package io.jhchoe.familytree.core.family.adapter.in.request;

import io.jhchoe.familytree.core.family.domain.FamilyMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 구성원 역할 변경을 위한 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
public class UpdateFamilyMemberRoleRequest {
    
    @NotNull(message = "변경할 역할은 필수입니다.")
    private FamilyMemberRole role;
    
    /**
     * 요청 객체를 생성합니다.
     *
     * @param role 변경할 역할
     */
    public UpdateFamilyMemberRoleRequest(FamilyMemberRole role) {
        this.role = role;
    }
}
