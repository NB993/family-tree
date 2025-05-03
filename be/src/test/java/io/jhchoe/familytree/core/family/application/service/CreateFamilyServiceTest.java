package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.core.family.application.port.in.CreateFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.out.CreateFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] FamilyService")
@ExtendWith(MockitoExtension.class)
class CreateFamilyServiceTest {

    @Mock
    private CreateFamilyPort createFamilyPort;

    @InjectMocks
    private CreateFamilyService sut;

    @Test
    @DisplayName("create 메서드는 정상적인 CreateFamilyCommand를 입력받아 저장 후 Family ID를 반환해야 한다.")
    void given_valid_command_when_create_then_return_family_id() {
        // given
        Long expectedId = 1L;
        CreateFamilyCommand command = new CreateFamilyCommand("Family Name", "http://example.com/profile",
            "Family Description");
        Family family = Family.newFamily(command.getName(), command.getDescription(), command.getProfileUrl());
        when(createFamilyPort.create(any(Family.class))).thenReturn(expectedId);

        // when
        Long result = sut.create(command);

        // then
        assertThat(result).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("create 메서드는 CreateFamilyCommand가 null이면 예외를 발생시켜야 한다.")
    void given_null_command_when_create_then_throw_exception() {
        // given
        CreateFamilyCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.create(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("create 메서드는 이름이 없는 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_null_name_when_create_then_throw_exception() {
        // given
        assertThatThrownBy(() -> new CreateFamilyCommand(null, "http://example.com", "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 이름을 입력해주세요.");
    }

    @Test
    @DisplayName("create 메서드는 이름이 빈 문자열일 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_blank_name_when_create_then_throw_exception() {
        // given
        assertThatThrownBy(() -> new CreateFamilyCommand("   ", "http://example.com", "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 이름을 입력해주세요.");
    }

    @Test
    @DisplayName("create 메서드는 이름이 100자를 초과하는 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_name_over_100_chars_when_create_then_throw_exception() {
        // given
        String longName = "a".repeat(101);

        // when & then
        assertThatThrownBy(() -> new CreateFamilyCommand(longName, null, "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 이름은 100자 이내로 작성해주세요.");
    }

    @Test
    @DisplayName("create 메서드는 유효하지 않은 profileUrl이 주어진 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_invalid_profile_url_when_create_then_throw_exception() {
        // given
        assertThatThrownBy(() -> new CreateFamilyCommand("Family Name", "invalid-url", "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("프로필 URL 형식이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("create 메서드는 설명이 200자를 초과할 경우 IllegalArgumentException을 발생시켜야 한다.")
    void given_command_with_description_over_200_chars_when_create_then_throw_exception() {
        // given
        String longDescription = "a".repeat(201);

        // when & then
        assertThatThrownBy(() -> new CreateFamilyCommand("Family Name", "http://example.com", longDescription))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Family 설명은 200자 이내로 작성해주세요.");
    }
}
