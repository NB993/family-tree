package io.jhchoe.familytree.core.family.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class FindFamilyByNameLikeQueryTest {

    @Test
    @DisplayName("유효한 이름으로 쿼리 객체를 생성할 수 있다")
    void create_query_with_valid_name() {
        // given
        String validName = "테스트 가족";

        // when
        FindFamilyByNameLikeQuery query = new FindFamilyByNameLikeQuery(validName);

        // then
        assertThat(query.getName()).isEqualTo(validName);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름이 null 또는 빈 문자열인 경우 예외가 발생한다")
    void create_query_with_null_and_empty_name(String nullAndEmptyName) {
        assertThatThrownBy(() -> new FindFamilyByNameLikeQuery(nullAndEmptyName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Family 이름은 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",            // 빈 문자열
        " ",           // 스페이스 한 개
        "   ",         // 스페이스 여러 개
        "\t",          // 탭
        "\n",          // 줄바꿈
        "\r",          // 캐리지 리턴
        "\f",          // 폼 피드
        " \t\n\r\f "   // 다양한 공백 문자 조합
    })
    @DisplayName("이름이 공백 문자열인 경우 예외가 발생한다")
    void create_query_with_blank_name(String blankName) {
        assertThatThrownBy(() -> new FindFamilyByNameLikeQuery(blankName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Family 이름은 필수입니다.");
    }
}
