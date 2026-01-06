package io.jhchoe.familytree.core.family.domain;

/**
 * 생일의 양력/음력 타입을 정의하는 열거형 클래스입니다.
 * 카카오 OAuth에서 제공하는 birthday_type 값과 매핑됩니다.
 */
public enum BirthdayType {

    /**
     * 양력
     */
    SOLAR("양력"),

    /**
     * 음력
     */
    LUNAR("음력");

    private final String displayName;

    BirthdayType(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * 표시용 이름을 반환합니다.
     *
     * @return 표시용 이름 (양력/음력)
     */
    public String getDisplayName() {
        return displayName;
    }
}
