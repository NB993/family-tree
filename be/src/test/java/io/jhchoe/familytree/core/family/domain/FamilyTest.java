package io.jhchoe.familytree.core.family.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[Unit Test] Family")
class FamilyTest {

    @Test
    @DisplayName("newFamily 메서드로 새로운 Family 객체를 생성한다.")
    void should_create_new_family_with_new_family_method() {
        // given
        String name = "우리가족";
        String description = "행복한 가족";
        String profileUrl = "http://example.com/profile.jpg";
        Boolean isPublic = true;

        // when
        Family family = Family.newFamily(name, description, profileUrl, isPublic);

        // then
        assertNull(family.getId());
        assertEquals("우리가족", family.getName());
        assertEquals("행복한 가족", family.getDescription());
        assertEquals("http://example.com/profile.jpg", family.getProfileUrl());
        assertEquals(true, family.getIsPublic());
        assertNull(family.getCreatedBy());
        assertNull(family.getCreatedAt());
        assertNull(family.getModifiedBy());
        assertNull(family.getModifiedAt());
    }

    @Test
    @DisplayName("withId 메서드로 ID와 모든 정보를 포함하는 Family 객체를 생성한다.")
    void should_create_family_with_id_and_all_details() {
        // given
        Long id = 1L;
        String name = "우리가족";
        String description = "행복한 가족";
        String profileUrl = "http://example.com/profile.jpg";
        Boolean isPublic = false;
        Long createdBy = 100L;
        LocalDateTime createdAt = LocalDateTime.now();
        Long modifiedBy = 200L;
        LocalDateTime modifiedAt = LocalDateTime.now();

        // when
        Family family = Family.withId(id, name, description, profileUrl, isPublic, 
                                     createdBy, createdAt, modifiedBy, modifiedAt);

        // then
        assertEquals(1L, family.getId());
        assertEquals("우리가족", family.getName());
        assertEquals("행복한 가족", family.getDescription());
        assertEquals("http://example.com/profile.jpg", family.getProfileUrl());
        assertEquals(false, family.getIsPublic());
        assertEquals(100L, family.getCreatedBy());
        assertEquals(createdAt, family.getCreatedAt());
        assertEquals(200L, family.getModifiedBy());
        assertEquals(modifiedAt, family.getModifiedAt());
    }

    @Test
    @DisplayName("update 메서드로 Family 정보를 업데이트한다.")
    void should_update_family_information() {
        // given
        Family family = Family.newFamily("기존가족", "기존설명", "http://old.com", true);
        String newName = "새로운가족";
        String newDescription = "새로운설명";
        String newProfileUrl = "http://new.com";
        Boolean newIsPublic = false;

        // when
        family.update(newName, newDescription, newProfileUrl, newIsPublic);

        // then
        assertEquals("새로운가족", family.getName());
        assertEquals("새로운설명", family.getDescription());
        assertEquals("http://new.com", family.getProfileUrl());
        assertEquals(false, family.getIsPublic());
    }

    @Test
    @DisplayName("isPublic이 null인 경우에도 Family 객체가 생성된다.")
    void should_create_family_when_is_public_is_null() {
        // given
        String name = "우리가족";
        String description = "행복한 가족";
        String profileUrl = "http://example.com/profile.jpg";
        Boolean isPublic = null;

        // when
        Family family = Family.newFamily(name, description, profileUrl, isPublic);

        // then
        assertEquals("우리가족", family.getName());
        assertEquals("행복한 가족", family.getDescription());
        assertEquals("http://example.com/profile.jpg", family.getProfileUrl());
        assertNull(family.getIsPublic());
    }
}
