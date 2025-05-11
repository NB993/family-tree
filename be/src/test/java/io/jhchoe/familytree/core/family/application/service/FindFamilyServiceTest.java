package io.jhchoe.familytree.core.family.application.service;

import static org.mockito.Mockito.when;

import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
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

        Assertions.assertThat(family).isNotNull();
        Assertions.assertThat(family.getId()).isEqualTo(expectedFamily.getId());
        Assertions.assertThat(family.getName()).isEqualTo(expectedFamily.getName());
        Assertions.assertThat(family.getDescription()).isEqualTo(expectedFamily.getDescription());
        Assertions.assertThat(family.getProfileUrl()).isEqualTo(expectedFamily.getProfileUrl());
        Assertions.assertThat(family.getCreatedBy()).isEqualTo(expectedFamily.getCreatedBy());
        Assertions.assertThat(family.getCreatedAt()).isEqualTo(expectedFamily.getCreatedAt());
        Assertions.assertThat(family.getModifiedBy()).isEqualTo(expectedFamily.getModifiedBy());
        Assertions.assertThat(family.getModifiedAt()).isEqualTo(expectedFamily.getModifiedAt());
    }
}
