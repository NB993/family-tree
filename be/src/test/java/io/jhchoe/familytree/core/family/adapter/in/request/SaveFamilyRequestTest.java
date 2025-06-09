package io.jhchoe.familytree.core.family.adapter.in.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Unit Test] SaveFamilyRequest Validation")
class SaveFamilyRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 한글 가족명으로 요청할 때 검증을 통과한다")
    void should_pass_validation_when_korean_family_name_is_valid() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            "김가족",
            "PUBLIC",
            "우리 가족입니다",
            "https://example.com/profile.jpg"
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("유효한 영문 가족명으로 요청할 때 검증을 통과한다")
    void should_pass_validation_when_english_family_name_is_valid() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            "Smith Family",
            "PRIVATE",
            "Our family",
            null
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("이모지가 포함된 가족명으로 요청할 때 검증을 통과한다")
    void should_pass_validation_when_family_name_contains_emoji() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            "우리가족👨‍👩‍👧‍👦",
            "PUBLIC",
            "행복한 가족 😊",
            null
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("가족명이 null일 때 검증에 실패한다")
    void should_fail_validation_when_family_name_is_null() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            null,
            "PUBLIC",
            "설명",
            null
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("가족명을 입력해주세요");
    }

    @Test
    @DisplayName("가족명이 공백일 때 검증에 실패한다")
    void should_fail_validation_when_family_name_is_blank() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            "   ",
            "PUBLIC",
            "설명",
            null
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("가족명을 입력해주세요");
    }

    @Test
    @DisplayName("가족명이 20자를 초과할 때 검증에 실패한다")
    void should_fail_validation_when_family_name_exceeds_max_length() {
        // given
        String longName = "가".repeat(21); // 21자
        SaveFamilyRequest request = new SaveFamilyRequest(
            longName,
            "PUBLIC",
            "설명",
            null
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("가족명은 20자 이하로 입력해주세요");
    }

    @Test
    @DisplayName("공개여부가 PRIVATE도 PUBLIC도 아닐 때 검증에 실패한다")
    void should_fail_validation_when_visibility_is_invalid() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            "김가족",
            "INVALID",
            "설명",
            null
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("공개 여부는 PUBLIC 또는 PRIVATE만 가능합니다");
    }

    @Test
    @DisplayName("다양한 이모지 조합이 포함된 가족명으로 요청할 때 검증을 통과한다")
    void should_pass_validation_when_family_name_contains_various_emoji() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            "Happy Family 🏠❤️🌟",
            "PUBLIC",
            "We love each other! 💕",
            null
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("숫자가 포함된 가족명으로 요청할 때 검증을 통과한다")
    void should_pass_validation_when_family_name_contains_numbers() {
        // given
        SaveFamilyRequest request = new SaveFamilyRequest(
            "김가족2024",
            "PUBLIC",
            "새로운 시작",
            null
        );

        // when
        Set<ConstraintViolation<SaveFamilyRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }
}
