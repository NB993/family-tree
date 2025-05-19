package io.jhchoe.familytree.core.family.application.port.in;

import java.util.Objects;
import lombok.Getter;

/**
 * Family 가입 신청을 위한 커맨드 객체입니다.
 */
@Getter
public class SaveFamilyJoinRequestCommand {
    private final Long familyId;
    private final Long requesterId;

    /**
     * Family 가입 신청 커맨드 객체를 생성합니다.
     *
     * @param familyId 가입 신청할 Family의 ID
     * @param requesterId 가입 신청자의 ID (FTUser ID)
     * @throws IllegalArgumentException 파라미터가 유효하지 않을 경우 예외 발생
     */
    public SaveFamilyJoinRequestCommand(Long familyId, Long requesterId) {
        validateFamilyId(familyId);
        validateRequesterId(requesterId);
        
        this.familyId = familyId;
        this.requesterId = requesterId;
    }

    private void validateFamilyId(Long familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("가입할 Family ID는 필수입니다.");
        }
        if (familyId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 Family ID입니다.");
        }
    }

    private void validateRequesterId(Long requesterId) {
        if (requesterId == null) {
            throw new IllegalArgumentException("신청자 ID는 필수입니다.");
        }
        if (requesterId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 신청자 ID입니다.");
        }
    }
}
