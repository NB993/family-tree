package io.jhchoe.familytree.core.family.adapter.in.request;

import io.jhchoe.familytree.core.family.domain.FamilyMemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 구성원 상태 변경을 위한 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
public class ModifyFamilyMemberStatusRequest {
    
    @NotNull(message = "변경할 상태는 필수입니다.")
    private FamilyMemberStatus status;
    
    private String reason;
    
    /**
     * 요청 객체를 생성합니다.
     *
     * @param status 변경할 상태
     * @param reason 변경 사유
     */
    public ModifyFamilyMemberStatusRequest(FamilyMemberStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }
}
