package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] FindFamilyServiceTest")
@ExtendWith(MockitoExtension.class)
public class FindFamilyServiceTest {

    @Mock
    private FindFamilyPort findFamilyPort;

    @InjectMocks
    private FindFamilyService sut;

    @Test
    @DisplayName("find 메서드는 id와 일치하는 Family를 찾아 반환해야 한다")
    void given_valid_id_when_find_ById_then_return_family() {
        // given
        Long familyId = 1L;
        Family expectedFamily = Family.withId(
            familyId,
            "name",
            "description",
            "profile",
            2L,
            LocalDateTime.now(),
            2L,
            LocalDateTime.now()
        );

        when(findFamilyPort.findById(familyId)).thenReturn(Optional.of(expectedFamily));

        // when
        Family family = sut.findById(familyId);

        // then
        assertThat(family).isNotNull();
        assertThat(family.getId()).isEqualTo(expectedFamily.getId());
        assertThat(family.getName()).isEqualTo(expectedFamily.getName());
        assertThat(family.getDescription()).isEqualTo(expectedFamily.getDescription());
        assertThat(family.getProfileUrl()).isEqualTo(expectedFamily.getProfileUrl());
        assertThat(family.getCreatedBy()).isEqualTo(expectedFamily.getCreatedBy());
        assertThat(family.getCreatedAt()).isEqualTo(expectedFamily.getCreatedAt());
        assertThat(family.getModifiedBy()).isEqualTo(expectedFamily.getModifiedBy());
        assertThat(family.getModifiedAt()).isEqualTo(expectedFamily.getModifiedAt());
    }

    @Test
    @DisplayName("find 메서드는 id가 null이면 예외를 발생시켜야 한다.")
    void given_null_id_when_find_ById_then_throw_exception() {
        // given
        Long familyId = null;

        // when & then
        assertThatThrownBy(() -> sut.findById(familyId))
            .isInstanceOf(FTException.class)
            .hasMessage("파라미터 누락.");
    }

    @Test
    @DisplayName("find 메서드는 id와 일치하는 Family를 찾지 못한 경우 예외를 발생시켜야 한다.")
    void given_invalid_id_when_find_ById_then_throw_exception() {
        // given
        Long notSavedFamilyId = 999L;

        // when
        when(findFamilyPort.findById(notSavedFamilyId))
            .thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> sut.findById(notSavedFamilyId))
            .isInstanceOf(FTException.class)
            .hasMessage("대상을 찾지 못했습니다.");
    }
}
