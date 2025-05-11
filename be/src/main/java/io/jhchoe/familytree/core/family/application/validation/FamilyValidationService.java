package io.jhchoe.familytree.core.family.application.validation;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FamilyValidationService {

    private final FindFamilyPort findFamilyPort;
    private final FindFamilyMemberPort findFamilyMemberPort;

    public void validateFamilyExists(Long familyId) {
        Objects.requireNonNull(familyId, "familyId must not be null");

        if (!findFamilyPort.existsById(familyId)) {
            throw new FTException(CommonExceptionCode.NOT_FOUND, "familyId");
        }
    }

    public void validateFamilyAccess(Long familyId, Long userId) {
        Objects.requireNonNull(familyId, "familyId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        if (!findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, userId)) {
            throw new FTException(CommonExceptionCode.NOT_FOUND, "familyId, userId");
        }
    }

}
