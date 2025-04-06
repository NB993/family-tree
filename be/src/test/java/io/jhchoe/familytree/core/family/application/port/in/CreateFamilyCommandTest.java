package io.jhchoe.familytree.core.family.application.port.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Unit Test] CreateFamilyCommand")
class CreateFamilyCommandTest {

    @Test
    @DisplayName("name이 null일 경우 예외를 발생시킨다.")
    void should_throw_exception_when_name_is_null() {
        // given
        String name = null;
        String profileUrl = "http://example.com";
        String description = "Valid description";
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new CreateFamilyCommand(name, profileUrl, description)
        );
    
        // then
        assertEquals("Family 이름을 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("name이 공백일 경우 예외를 발생시킨다.")
    void should_throw_exception_when_name_is_blank() {
        // given
        String name = " ";
        String profileUrl = "http://example.com";
        String description = "Valid description";
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new CreateFamilyCommand(name, profileUrl, description)
        );
    
        // then
        assertEquals("Family 이름을 입력해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("name 길이가 100자를 초과할 경우 예외를 발생시킨다.")
    void should_throw_exception_when_name_exceeds_max_length() {
        // given
        String longName = "a".repeat(101); // 101 characters
        String profileUrl = "http://example.com";
        String description = "Valid description";
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new CreateFamilyCommand(longName, profileUrl, description)
        );
    
        // then
        assertEquals("Family 이름은 100자 이내로 작성해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("profileUrl이 유효하지 않을 경우 예외를 발생시킨다.")
    void should_throw_exception_when_profile_url_is_invalid() {
        // given
        String name = "Valid Name";
        String profileUrl = "ftp://example.com";
        String description = "Valid description";
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new CreateFamilyCommand(name, profileUrl, description)
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
    
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new CreateFamilyCommand(name, profileUrl, longDescription)
        );
    
        // then
        assertEquals("Family 설명은 200자 이내로 작성해주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("모든 필드가 유효할 때 CreateFamilyCommand 객체를 생성한다.")
    void should_create_command_when_all_fields_are_valid() {
        // given
        String name = "Valid Name";
        String profileUrl = "http://example.com";
        String description = "Valid description";
    
        // when
        CreateFamilyCommand command = new CreateFamilyCommand(name, profileUrl, description);
    
        // then
        assertEquals("Valid Name", command.getName());
        assertEquals("http://example.com", command.getProfileUrl());
        assertEquals("Valid description", command.getDescription());
    }

    @Test
    @DisplayName("profileUrl과 description이 null일 때도 CreateFamilyCommand 객체가 생성된다.")
    void should_create_command_when_optional_fields_are_null() {
        // given
        String name = "Valid Name";
        String profileUrl = null;
        String description = null;
    
        // when
        CreateFamilyCommand command = new CreateFamilyCommand(name, profileUrl, description);
    
        // then
        assertEquals("Valid Name", command.getName());
        assertNull(command.getProfileUrl());
        assertNull(command.getDescription());
    }
}
