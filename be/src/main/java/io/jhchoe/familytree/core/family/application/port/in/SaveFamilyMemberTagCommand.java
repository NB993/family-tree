package io.jhchoe.familytree.core.family.application.port.in;

import java.util.regex.Pattern;

/**
 * 태그 생성 명령 객체입니다.
 *
 * @param familyId 가족 ID (필수)
 * @param name     태그 이름 (필수, 1~10자, 한글/영문/숫자/이모지/공백만 허용)
 */
public record SaveFamilyMemberTagCommand(Long familyId, String name) {

    private static final int MAX_NAME_LENGTH = 10;
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[\\p{L}\\p{N}\\p{Emoji}\\p{Emoji_Component}\\s]+$",
        Pattern.UNICODE_CHARACTER_CLASS
    );

    /**
     * 태그 생성 명령 객체를 생성합니다.
     *
     * @param familyId 가족 ID
     * @param name     태그 이름
     * @throws IllegalArgumentException 유효성 검증 실패 시
     */
    public SaveFamilyMemberTagCommand {
        validateFamilyId(familyId);
        validateName(name);
    }

    private static void validateFamilyId(Long familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("가족 ID는 필수입니다.");
        }
        if (familyId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 가족 ID입니다.");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("태그 이름은 필수입니다.");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("태그 이름은 10자 이하여야 합니다.");
        }

        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new IllegalArgumentException("태그 이름에 허용되지 않는 문자가 포함되어 있습니다.");
        }
    }
}
