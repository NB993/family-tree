package io.jhchoe.familytree.common.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Unit Test] KakaoUserInfoTest")
class KakaoUserInfoTest {

    @Nested
    @DisplayName("getBirthDate 메서드")
    class GetBirthDateTest {

        @Test
        @DisplayName("birthday와 birthyear가 모두 존재하면 LocalDate를 반환합니다")
        void return_local_date_when_birthday_and_birthyear_exist() {
            // given
            Map<String, Object> account = new HashMap<>();
            account.put("birthday", "0115");  // MMDD 형식
            account.put("birthyear", "1990"); // YYYY 형식

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "12345");
            attributes.put("kakao_account", account);

            // when
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            LocalDate birthDate = kakaoUserInfo.getBirthDate();

            // then
            assertThat(birthDate).isNotNull();
            assertThat(birthDate.getYear()).isEqualTo(1990);
            assertThat(birthDate.getMonthValue()).isEqualTo(1);
            assertThat(birthDate.getDayOfMonth()).isEqualTo(15);
        }

        @Test
        @DisplayName("birthyear가 없으면 null을 반환합니다")
        void return_null_when_birthyear_is_missing() {
            // given
            Map<String, Object> account = new HashMap<>();
            account.put("birthday", "0115");  // MMDD만 있음

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "12345");
            attributes.put("kakao_account", account);

            // when
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            LocalDate birthDate = kakaoUserInfo.getBirthDate();

            // then
            assertThat(birthDate).isNull();
        }

        @Test
        @DisplayName("birthday가 없으면 null을 반환합니다")
        void return_null_when_birthday_is_missing() {
            // given
            Map<String, Object> account = new HashMap<>();
            account.put("birthyear", "1990");  // 연도만 있음

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "12345");
            attributes.put("kakao_account", account);

            // when
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            LocalDate birthDate = kakaoUserInfo.getBirthDate();

            // then
            assertThat(birthDate).isNull();
        }

        @Test
        @DisplayName("kakao_account가 없으면 null을 반환합니다")
        void return_null_when_kakao_account_is_missing() {
            // given
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "12345");

            // when
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            LocalDate birthDate = kakaoUserInfo.getBirthDate();

            // then
            assertThat(birthDate).isNull();
        }

        @Test
        @DisplayName("12월 25일 생년월일이 올바르게 파싱됩니다")
        void parse_december_birthday_correctly() {
            // given
            Map<String, Object> account = new HashMap<>();
            account.put("birthday", "1225");
            account.put("birthyear", "2000");

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "12345");
            attributes.put("kakao_account", account);

            // when
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            LocalDate birthDate = kakaoUserInfo.getBirthDate();

            // then
            assertThat(birthDate).isEqualTo(LocalDate.of(2000, 12, 25));
        }

        @Test
        @DisplayName("birthday가 숫자가 아닌 경우 null을 반환합니다")
        void return_null_when_birthday_is_not_numeric() {
            // given
            Map<String, Object> account = new HashMap<>();
            account.put("birthday", "abcd");
            account.put("birthyear", "1990");

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "12345");
            attributes.put("kakao_account", account);

            // when
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            LocalDate birthDate = kakaoUserInfo.getBirthDate();

            // then
            assertThat(birthDate).isNull();
        }

        @Test
        @DisplayName("유효하지 않은 날짜인 경우 null을 반환합니다")
        void return_null_when_date_is_invalid() {
            // given
            Map<String, Object> account = new HashMap<>();
            account.put("birthday", "1332");  // 13월 32일은 유효하지 않음
            account.put("birthyear", "1990");

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("id", "12345");
            attributes.put("kakao_account", account);

            // when
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(attributes);
            LocalDate birthDate = kakaoUserInfo.getBirthDate();

            // then
            assertThat(birthDate).isNull();
        }
    }
}
