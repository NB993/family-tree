package io.jhchoe.familytree.core.family.application.service;

import io.jhchoe.familytree.core.family.application.port.in.FamilyNameAvailabilityResult;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * CheckFamilyNameDuplicationService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unit Test] CheckFamilyNameDuplicationService")
class CheckFamilyNameDuplicationServiceTest {

    @Mock
    private FindFamilyPort findFamilyPort;

    @InjectMocks
    private CheckFamilyNameDuplicationService checkFamilyNameDuplicationService;

    @Test
    @DisplayName("사용 가능한 가족명인 경우 available true를 반환한다")
    void should_return_available_true_when_family_name_is_not_duplicated() {
        // given
        String familyName = "새로운가족";
        given(findFamilyPort.findByName(familyName)).willReturn(Optional.empty());

        // when
        FamilyNameAvailabilityResult result = checkFamilyNameDuplicationService.checkDuplication(familyName);

        // then
        assertThat(result.available()).isTrue();
        assertThat(result.message()).isEqualTo("사용 가능한 가족명입니다");
        verify(findFamilyPort).findByName(familyName);
    }

    @Test
    @DisplayName("이미 존재하는 가족명은 available false를 반환한다")
    void should_return_available_false_when_family_name_already_exists() {
        // given
        String familyName = "기존가족";
        Family existingFamily = Family.withId(
            1L,
            familyName,
            "기존 가족 설명",
            "http://example.com/profile.jpg",
            true,
            1L,
            LocalDateTime.now(),
            1L,
            LocalDateTime.now()
        );
        
        given(findFamilyPort.findByName(familyName)).willReturn(Optional.of(existingFamily));

        // when
        FamilyNameAvailabilityResult result = checkFamilyNameDuplicationService.checkDuplication(familyName);

        // then
        assertThat(result.available()).isFalse();
        assertThat(result.message()).isEqualTo("이미 사용 중인 가족명입니다");
        verify(findFamilyPort).findByName(familyName);
    }

    @Test
    @DisplayName("가족명이 null인 경우 IllegalArgumentException을 던진다")
    void should_throw_exception_when_family_name_is_null() {
        // given
        String familyName = null;

        // when & then
        assertThatThrownBy(() -> checkFamilyNameDuplicationService.checkDuplication(familyName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가족명은 필수값입니다");
    }

    @Test
    @DisplayName("가족명이 빈 문자열인 경우 IllegalArgumentException을 던진다")
    void should_throw_exception_when_family_name_is_empty() {
        // given
        String familyName = "";

        // when & then
        assertThatThrownBy(() -> checkFamilyNameDuplicationService.checkDuplication(familyName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가족명은 필수값입니다");
    }

    @Test
    @DisplayName("가족명이 공백만으로 구성된 경우 IllegalArgumentException을 던진다")
    void should_throw_exception_when_family_name_is_only_whitespace() {
        // given
        String familyName = "   ";

        // when & then
        assertThatThrownBy(() -> checkFamilyNameDuplicationService.checkDuplication(familyName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가족명은 필수값입니다");
    }

    @Test
    @DisplayName("가족명이 20자를 초과하는 경우 IllegalArgumentException을 던진다")
    void should_throw_exception_when_family_name_exceeds_max_length() {
        // given
        String familyName = "a".repeat(21); // 21자

        // when & then
        assertThatThrownBy(() -> checkFamilyNameDuplicationService.checkDuplication(familyName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가족명은 20자를 초과할 수 없습니다");
    }

    @Test
    @DisplayName("정확히 20자인 가족명은 정상적으로 처리된다")
    void should_process_family_name_with_max_length_successfully() {
        // given
        String familyName = "a".repeat(20); // 정확히 20자
        given(findFamilyPort.findByName(familyName)).willReturn(Optional.empty());

        // when
        FamilyNameAvailabilityResult result = checkFamilyNameDuplicationService.checkDuplication(familyName);

        // then
        assertThat(result.available()).isTrue();
        assertThat(result.message()).isEqualTo("사용 가능한 가족명입니다");
        verify(findFamilyPort).findByName(familyName);
    }
}
