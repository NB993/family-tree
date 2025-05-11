package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.core.family.application.port.in.SaveFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.out.SaveFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] FamilyService")
@ExtendWith(MockitoExtension.class)
class SaveFamilyServiceTest {

    @Mock
    private SaveFamilyPort saveFamilyPort;

    @InjectMocks
    private SaveFamilyService sut;

    @Test
    @DisplayName("create 메서드는 정상적인 CreateFamilyCommand를 입력받아 저장 후 Family ID를 반환해야 한다.")
    void given_valid_command_when_save_then_return_family_id() {
        // given
        Long expectedId = 1L;
        SaveFamilyCommand command = new SaveFamilyCommand("Family Name", "http://example.com/profile",
            "Family Description");
        Family family = Family.newFamily(command.getName(), command.getDescription(), command.getProfileUrl());
        when(saveFamilyPort.save(any(Family.class))).thenReturn(expectedId);

        // when
        Long result = sut.save(command);

        // then
        assertThat(result).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("create 메서드는 CreateFamilyCommand가 null이면 예외를 발생시켜야 한다.")
    void given_null_command_when_save_then_throw_exception() {
        // given
        SaveFamilyCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.save(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("create 메서드는 이름이 없는 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_null_name_when_save_then_throw_exception() {
        // given
        assertThatThrownBy(() -> new SaveFamilyCommand(null, "http://example.com", "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 이름을 입력해주세요.");
    }

    @Test
    @DisplayName("create 메서드는 이름이 빈 문자열일 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_blank_name_when_save_then_throw_exception() {
        // given
        assertThatThrownBy(() -> new SaveFamilyCommand("   ", "http://example.com", "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 이름을 입력해주세요.");
    }

    @Test
    @DisplayName("create 메서드는 이름이 100자를 초과하는 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_name_over_100_chars_when_save_then_throw_exception() {
        // given
        String longName = "a".repeat(101);

        // when & then
        assertThatThrownBy(() -> new SaveFamilyCommand(longName, null, "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 이름은 100자 이내로 작성해주세요.");
    }

    @Test
    @DisplayName("create 메서드는 유효하지 않은 profileUrl이 주어진 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_invalid_profile_url_when_save_then_throw_exception() {
        // given
        assertThatThrownBy(() -> new SaveFamilyCommand("Family Name", "invalid-url", "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("프로필 URL 형식이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("create 메서드는 설명이 200자를 초과할 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_description_over_200_chars_when_save_then_throw_exception() {
        // given
        String longDescription = "a".repeat(201);

        // when & then
        assertThatThrownBy(() -> new SaveFamilyCommand("Family Name", "http://example.com", longDescription))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 설명은 200자 이내로 작성해주세요.");
    }
}
