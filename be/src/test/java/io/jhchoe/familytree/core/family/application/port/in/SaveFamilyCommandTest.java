package io.jhchoe.familytree.core.family.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Unit Test] SaveFamilyCommand")
class SaveFamilyCommandTest {

    @Test
    @DisplayName("name이 null일 경우 예외를 발생시킨다.")
    void should_throw_exception_when_name_is_null() {
        // given
        String name = null;
        String profileUrl = "http://example.com";
        String description = "Valid description";
        Boolean isPublic = true;
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new SaveFamilyCommand(name, profileUrl, description, isPublic)
        );
    
        // then
        assertEquals("가족명을 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("name이 공백일 경우 예외를 발생시킨다.")
    void should_throw_exception_when_name_is_blank() {
        // given
        String name = " ";
        String profileUrl = "http://example.com";
        String description = "Valid description";
        Boolean isPublic = true;
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new SaveFamilyCommand(name, profileUrl, description, isPublic)
        );
    
        // then
        assertEquals("가족명을 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("name 길이가 20자를 초과할 경우 예외를 발생시킨다.")
    void should_throw_exception_when_name_exceeds_max_length() {
        // given
        String longName = "가".repeat(21); // 21 characters
        String profileUrl = "http://example.com";
        String description = "Valid description";
        Boolean isPublic = true;
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new SaveFamilyCommand(longName, profileUrl, description, isPublic)
        );
    
        // then
        assertEquals("가족명은 20자 이하로 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("name에 특수문자가 포함될 경우 예외를 발생시킨다.")
    void should_throw_exception_when_name_contains_special_characters() {
        // given
        String nameWithSpecialChars = "김가족@#$";
        String profileUrl = "http://example.com";
        String description = "Valid description";
        Boolean isPublic = true;
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new SaveFamilyCommand(nameWithSpecialChars, profileUrl, description, isPublic)
        );
    
        // then
        assertEquals("가족명에는 한글, 영문, 숫자, 공백, 이모지만 사용 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("name에 이모지가 포함된 경우 정상적으로 생성된다.")
    void should_create_command_when_name_contains_emoji() {
        // given
        String nameWithEmoji = "우리가족👨‍👩‍👧‍👦";
        String profileUrl = "http://example.com";
        String description = "Valid description";
        Boolean isPublic = true;
    
        // when
        SaveFamilyCommand command = new SaveFamilyCommand(nameWithEmoji, profileUrl, description, isPublic);
    
        // then
        assertEquals("우리가족👨‍👩‍👧‍👦", command.getName());
        assertEquals("http://example.com", command.getProfileUrl());
        assertEquals("Valid description", command.getDescription());
        assertEquals(true, command.getIsPublic());
    }

    @Test
    @DisplayName("profileUrl이 유효하지 않을 경우 예외를 발생시킨다.")
    void should_throw_exception_when_profile_url_is_invalid() {
        // given
        String name = "Valid Name";
        String profileUrl = "ftp://example.com";
        String description = "Valid description";
        Boolean isPublic = true;
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new SaveFamilyCommand(name, profileUrl, description, isPublic)
        );
    
        // then
        assertEquals("프로필 URL 형식이 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("description 길이가 200자를 초과할 경우 예외를 발생시킨다.")
    void should_throw_exception_when_description_exceeds_max_length() {
        // given
        String name = "Valid Name";
        String profileUrl = "http://example.com";
        String longDescription = "a".repeat(201); // 201 characters
        Boolean isPublic = true;
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new SaveFamilyCommand(name, profileUrl, longDescription, isPublic)
        );
    
        // then
        assertEquals("Family 설명은 200자 이내로 작성해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("isPublic이 null일 경우 예외를 발생시킨다.")
    void should_throw_exception_when_is_public_is_null() {
        // given
        String name = "Valid Name";
        String profileUrl = "http://example.com";
        String description = "Valid description";
        Boolean isPublic = null;
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new SaveFamilyCommand(name, profileUrl, description, isPublic)
        );
    
        // then
        assertEquals("공개 여부를 선택해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("모든 필드가 유효할 때 SaveFamilyCommand 객체를 생성한다.")
    void should_create_command_when_all_fields_are_valid() {
        // given
        String name = "Valid Name";
        String profileUrl = "http://example.com";
        String description = "Valid description";
        Boolean isPublic = true;
    
        // when
        SaveFamilyCommand command = new SaveFamilyCommand(name, profileUrl, description, isPublic);
    
        // then
        assertEquals("Valid Name", command.getName());
        assertEquals("http://example.com", command.getProfileUrl());
        assertEquals("Valid description", command.getDescription());
        assertEquals(true, command.getIsPublic());
    }

    @Test
    @DisplayName("profileUrl과 description이 null일 때도 SaveFamilyCommand 객체가 생성된다.")
    void should_create_command_when_optional_fields_are_null() {
        // given
        String name = "Valid Name";
        String profileUrl = null;
        String description = null;
        Boolean isPublic = false;
    
        // when
        SaveFamilyCommand command = new SaveFamilyCommand(name, profileUrl, description, isPublic);
    
        // then
        assertEquals("Valid Name", command.getName());
        assertNull(command.getProfileUrl());
        assertNull(command.getDescription());
        assertEquals(false, command.getIsPublic());
    }
}
