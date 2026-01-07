package io.jhchoe.familytree.core.family.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;
import lombok.Getter;

/**
 * FamilyMemberTag 클래스는 FamilyMember를 분류하기 위한 태그 도메인입니다.
 * <p>
 * 한 Family 내에서 구성원들을 "친가 어른들", "외가", "조카들" 등으로 묶어서 관리할 수 있습니다.
 * 태그는 Family 단위로 관리되며, 한 멤버에 여러 태그, 한 태그에 여러 멤버가 연결될 수 있습니다.
 */
@Getter
public class FamilyMemberTag {

    /**
     * 태그 색상 팔레트 (노션 스타일, 9가지 색상).
     */
    public static final List<String> COLOR_PALETTE = List.of(
        "#E3E2E0", // Light Gray
        "#EEE0DA", // Brown
        "#FADEC9", // Orange
        "#FDECC8", // Yellow
        "#DBEDDB", // Green
        "#D3E5EF", // Blue
        "#E8DEEE", // Purple
        "#F5E0E9", // Pink
        "#FFE2DD"  // Red
    );

    /**
     * 태그 이름 유효성 검증 패턴.
     * 한글, 영문, 숫자, 이모지, 공백만 허용.
     */
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[\\p{L}\\p{N}\\p{Emoji}\\p{Emoji_Component}\\s]+$",
        Pattern.UNICODE_CHARACTER_CLASS
    );

    private static final int MAX_NAME_LENGTH = 10;
    private static final int MIN_NAME_LENGTH = 1;

    private final Long id;
    private final Long familyId;
    private final String name;
    private final String color;
    private final Long createdBy;
    private final LocalDateTime createdAt;
    private final Long modifiedBy;
    private final LocalDateTime modifiedAt;

    /**
     * FamilyMemberTag 생성자 (private).
     *
     * @param id         고유 ID
     * @param familyId   Family ID
     * @param name       태그 이름
     * @param color      태그 색상 (HEX 코드)
     * @param createdBy  생성한 사용자 ID
     * @param createdAt  생성 일시
     * @param modifiedBy 수정한 사용자 ID
     * @param modifiedAt 수정 일시
     */
    private FamilyMemberTag(
        Long id,
        Long familyId,
        String name,
        String color,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.familyId = familyId;
        this.name = name;
        this.color = color;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
    }

    /**
     * 새로운 태그를 생성합니다.
     * 색상은 COLOR_PALETTE에서 랜덤으로 배정됩니다.
     *
     * @param familyId  Family ID
     * @param name      태그 이름 (1~10자, 한글/영문/숫자/이모지/공백만 허용)
     * @param createdBy 생성한 사용자 ID
     * @return 새로운 FamilyMemberTag 인스턴스 (ID 없음)
     * @throws NullPointerException     familyId 또는 name이 null인 경우
     * @throws IllegalArgumentException name이 유효하지 않은 경우
     */
    public static FamilyMemberTag newTag(Long familyId, String name, Long createdBy) {
        Objects.requireNonNull(familyId, "familyId는 null일 수 없습니다");
        Objects.requireNonNull(name, "name은 null일 수 없습니다");
        Objects.requireNonNull(createdBy, "createdBy는 null일 수 없습니다");

        validateName(name);

        String randomColor = selectRandomColor();
        LocalDateTime now = LocalDateTime.now();

        return new FamilyMemberTag(
            null, familyId, name.trim(), randomColor, createdBy, now, null, null
        );
    }

    /**
     * 기존 태그를 복원합니다.
     * DB에서 조회한 데이터를 도메인 객체로 복원할 때 사용합니다.
     *
     * @param id         고유 ID
     * @param familyId   Family ID
     * @param name       태그 이름
     * @param color      태그 색상
     * @param createdBy  생성한 사용자 ID
     * @param createdAt  생성 일시
     * @param modifiedBy 수정한 사용자 ID
     * @param modifiedAt 수정 일시
     * @return FamilyMemberTag 인스턴스
     * @throws NullPointerException id가 null인 경우
     */
    public static FamilyMemberTag withId(
        Long id,
        Long familyId,
        String name,
        String color,
        Long createdBy,
        LocalDateTime createdAt,
        Long modifiedBy,
        LocalDateTime modifiedAt
    ) {
        Objects.requireNonNull(id, "id는 null일 수 없습니다");
        Objects.requireNonNull(familyId, "familyId는 null일 수 없습니다");
        Objects.requireNonNull(name, "name은 null일 수 없습니다");
        Objects.requireNonNull(color, "color는 null일 수 없습니다");

        return new FamilyMemberTag(
            id, familyId, name, color, createdBy, createdAt, modifiedBy, modifiedAt
        );
    }

    /**
     * 태그 이름을 변경합니다.
     *
     * @param newName    새로운 태그 이름
     * @param modifiedBy 수정한 사용자 ID
     * @return 이름이 변경된 새로운 FamilyMemberTag 객체
     * @throws NullPointerException     newName이 null인 경우
     * @throws IllegalArgumentException newName이 유효하지 않은 경우
     */
    public FamilyMemberTag rename(String newName, Long modifiedBy) {
        Objects.requireNonNull(newName, "newName은 null일 수 없습니다");
        Objects.requireNonNull(modifiedBy, "modifiedBy는 null일 수 없습니다");

        validateName(newName);

        return new FamilyMemberTag(
            this.id, this.familyId, newName.trim(), this.color,
            this.createdBy, this.createdAt, modifiedBy, LocalDateTime.now()
        );
    }

    /**
     * 태그 색상을 변경합니다.
     *
     * @param newColor   새로운 색상 (COLOR_PALETTE에 있어야 함)
     * @param modifiedBy 수정한 사용자 ID
     * @return 색상이 변경된 새로운 FamilyMemberTag 객체
     * @throws NullPointerException     newColor가 null인 경우
     * @throws IllegalArgumentException newColor가 COLOR_PALETTE에 없는 경우
     */
    public FamilyMemberTag changeColor(String newColor, Long modifiedBy) {
        Objects.requireNonNull(newColor, "newColor는 null일 수 없습니다");
        Objects.requireNonNull(modifiedBy, "modifiedBy는 null일 수 없습니다");

        validateColor(newColor);

        return new FamilyMemberTag(
            this.id, this.familyId, this.name, newColor,
            this.createdBy, this.createdAt, modifiedBy, LocalDateTime.now()
        );
    }

    /**
     * 태그 이름 유효성을 검증합니다.
     *
     * @param name 검증할 태그 이름
     * @throws IllegalArgumentException 유효하지 않은 경우
     */
    private static void validateName(String name) {
        String trimmedName = name.trim();

        if (trimmedName.isEmpty() || trimmedName.length() < MIN_NAME_LENGTH) {
            throw new IllegalArgumentException(
                String.format("태그 이름은 %d자 이상이어야 합니다", MIN_NAME_LENGTH)
            );
        }

        if (trimmedName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                String.format("태그 이름은 %d자 이하여야 합니다", MAX_NAME_LENGTH)
            );
        }

        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new IllegalArgumentException(
                "태그 이름에 허용되지 않는 문자가 포함되어 있습니다"
            );
        }
    }

    /**
     * 태그 색상 유효성을 검증합니다.
     *
     * @param color 검증할 색상 코드
     * @throws IllegalArgumentException COLOR_PALETTE에 없는 경우
     */
    private static void validateColor(String color) {
        if (!COLOR_PALETTE.contains(color)) {
            throw new IllegalArgumentException("허용되지 않는 색상입니다: " + color);
        }
    }

    /**
     * COLOR_PALETTE에서 랜덤으로 색상을 선택합니다.
     *
     * @return 랜덤 선택된 색상 코드
     */
    private static String selectRandomColor() {
        Random random = new Random();
        return COLOR_PALETTE.get(random.nextInt(COLOR_PALETTE.size()));
    }
}
