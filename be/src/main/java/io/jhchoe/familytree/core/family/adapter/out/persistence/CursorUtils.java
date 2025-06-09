package io.jhchoe.familytree.core.family.adapter.out.persistence;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 커서 기반 페이징을 위한 유틸리티 클래스입니다.
 * 
 * <p>커서 값을 Base64로 인코딩/디코딩하여 안전하고 예측 불가능한 페이징을 제공합니다.
 * 커서는 "id:memberCount" 형식으로 구성되며, 정렬과 유니크성을 보장합니다.</p>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CursorUtils {

    private static final String CURSOR_DELIMITER = ":";

    /**
     * Family ID와 memberCount를 조합하여 커서를 생성합니다.
     *
     * @param familyId Family의 고유 ID
     * @param memberCount Family의 구성원 수
     * @return Base64로 인코딩된 커서 문자열
     * @throws IllegalArgumentException familyId가 null이거나 memberCount가 음수인 경우
     */
    public static String encodeCursor(Long familyId, int memberCount) {
        if (familyId == null) {
            throw new IllegalArgumentException("familyId must not be null");
        }
        if (memberCount < 0) {
            throw new IllegalArgumentException("memberCount must not be negative");
        }

        String cursorValue = memberCount + CURSOR_DELIMITER + familyId;
        return Base64.getEncoder().encodeToString(cursorValue.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64로 인코딩된 커서를 디코딩하여 Family ID와 memberCount를 추출합니다.
     *
     * @param cursor Base64로 인코딩된 커서 문자열
     * @return CursorInfo 객체 (memberCount와 familyId 포함)
     * @throws IllegalArgumentException 유효하지 않은 커서 형식인 경우
     */
    public static CursorInfo decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            throw new IllegalArgumentException("cursor must not be null or blank");
        }

        try {
            String decodedValue = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decodedValue.split(CURSOR_DELIMITER);
            
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid cursor format");
            }

            int memberCount = Integer.parseInt(parts[0]);
            Long familyId = Long.parseLong(parts[1]);

            return new CursorInfo(memberCount, familyId);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to decode cursor: {}", cursor, e);
            throw new IllegalArgumentException("Invalid cursor format: " + cursor);
        }
    }

    /**
     * 커서에서 추출된 정보를 담는 레코드 클래스입니다.
     *
     * @param memberCount Family의 구성원 수
     * @param familyId Family의 고유 ID
     */
    public record CursorInfo(int memberCount, Long familyId) {
        
        public CursorInfo {
            if (familyId == null) {
                throw new IllegalArgumentException("familyId must not be null");
            }
            if (memberCount < 0) {
                throw new IllegalArgumentException("memberCount must not be negative");
            }
        }
    }
}
