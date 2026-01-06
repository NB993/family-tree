package io.jhchoe.familytree.core.family.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BirthdayType 열거형 클래스의 단위 테스트입니다.
 */
@DisplayName("[Unit Test] BirthdayTypeTest")
class BirthdayTypeTest {

    @Test
    @DisplayName("SOLAR 타입이 올바른 displayName을 가집니다")
    void solar_type_has_correct_display_name() {
        // given & when & then
        assertThat(BirthdayType.SOLAR.getDisplayName()).isEqualTo("양력");
    }

    @Test
    @DisplayName("LUNAR 타입이 올바른 displayName을 가집니다")
    void lunar_type_has_correct_display_name() {
        // given & when & then
        assertThat(BirthdayType.LUNAR.getDisplayName()).isEqualTo("음력");
    }

    @Test
    @DisplayName("모든 enum 값이 정의되어 있습니다")
    void all_enum_values_are_defined() {
        // given
        BirthdayType[] values = BirthdayType.values();

        // when & then
        assertThat(values).hasSize(2);
        assertThat(values).contains(BirthdayType.SOLAR, BirthdayType.LUNAR);
    }

    @Test
    @DisplayName("문자열로 enum 값을 찾을 수 있습니다")
    void can_find_enum_value_by_string() {
        // given & when & then
        assertThat(BirthdayType.valueOf("SOLAR")).isEqualTo(BirthdayType.SOLAR);
        assertThat(BirthdayType.valueOf("LUNAR")).isEqualTo(BirthdayType.LUNAR);
    }
}
