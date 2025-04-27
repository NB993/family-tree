package io.jhchoe.familytree.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Unit Test] MaskingUtils")
class MaskingUtilsTest {

    @DisplayName("이메일 마스킹: 정상 이메일 주소가 예상대로 마스킹 처리된다")
    @ParameterizedTest
    @CsvSource({
        "test@example.com, t***@e******.com",
        "a@b.com, a@b.com",
        "verylongemail@domain.com, v************@d*****.com",
        "john.doe@example.co.kr, j*******@e******.co.kr"
    })
    void maskEmail_withValidEmails_shouldReturnMaskedEmail(String email, String expected) {
        // when
        String result = MaskingUtils.maskEmail(email);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("이메일 마스킹: '@' 기호가 없는 문자열은 첫 글자만 보이고 나머지는 '*'로 마스킹된다")
    @ParameterizedTest
    @CsvSource({
        "invalidemail, i***********",
        "a, a",
        "ab, a*",
        "username, u*******"
    })
    void maskEmail_withoutAtSymbol_shouldMaskAllButFirstChar(String input, String expected) {
        // when
        String result = MaskingUtils.maskEmail(input);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("이메일 마스킹: null 또는 빈 문자열이 입력되면 빈 문자열을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void maskEmail_withNullOrEmpty_shouldReturnEmptyString(String email) {
        // when
        String result = MaskingUtils.maskEmail(email);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("이름 마스킹: 이름 길이에 따라 적절히 마스킹 처리된다")
    @ParameterizedTest
    @CsvSource({
        "홍길동, 홍*동",
        "김철수, 김*수",
        "이, 이",
        "박지, 박*",
        "Alexander, A*******r",
        "Kim, K*m"
    })
    void maskName_shouldMaskNameAccordingToLength(String name, String expected) {
        // when
        String result = MaskingUtils.maskName(name);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("이름 마스킹: null 또는 빈 문자열이 입력되면 빈 문자열을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void maskName_withNullOrEmpty_shouldReturnEmptyString(String name) {
        // when
        String result = MaskingUtils.maskName(name);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("이메일 마스킹: 도메인에 점(.)이 없는 비정상적인 이메일도 처리한다")
    void maskEmail_withInvalidDomain_shouldStillMask() {
        // given
        String invalidEmail = "user@localhost";

        // when
        String result = MaskingUtils.maskEmail(invalidEmail);

        // then
        assertThat(result).isEqualTo("u***@l********");
    }
}
