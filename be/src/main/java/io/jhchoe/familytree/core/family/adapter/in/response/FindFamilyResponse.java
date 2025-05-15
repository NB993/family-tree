package io.jhchoe.familytree.core.family.adapter.in.response;

import io.jhchoe.familytree.core.family.domain.Family;
import java.time.LocalDateTime;

/**
 * 가족 응답 DTO
 */
public record FindFamilyResponse(
   Long id,
   String name,
   String description,
   String profileUrl,
   Long createdBy,
   LocalDateTime createdAt,
   Long modifiedBy,
   LocalDateTime modifiedAt
) {

    public static FindFamilyResponse from(Family family) {
        return new FindFamilyResponse(
            family.getId(),
            family.getName(),
            family.getDescription(),
            family.getProfileUrl(),
            family.getCreatedBy(),
            family.getCreatedAt(),
            family.getModifiedBy(),
            family.getModifiedAt()
        );
    }
}
