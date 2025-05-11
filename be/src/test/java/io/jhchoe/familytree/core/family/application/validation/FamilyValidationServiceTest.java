package io.jhchoe.familytree.core.family.application.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyMemberPort;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] FamilyValidationServiceTest")
@ExtendWith(MockitoExtension.class)
class FamilyValidationServiceTest {

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private FindFamilyMemberPort findFamilyMemberPort;

    @InjectMocks
    private FamilyValidationService sut;

    @Test
    @DisplayName("validateFamilyExists 메서드는 id와 일치하는 Family의 존재 여부를 검증해야 한다.")
    void given_family_id_when_validate_family_exists_then_validate_successfully() {
        // given
        Long familyId = 1L;

        when(findFamilyPort.existsById(familyId))
            .thenReturn(true);

        // when & then
        Assertions.assertThatCode(() -> sut.validateFamilyExists(familyId))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateFamilyExists 메서드는 familyId가 null이면 예외를 발생시켜야 한다.")
    void given_null_id_when_validate_family_exists_then_throw_exception() {
        // given
        Long familyId = null;

        // when & then
        assertThatThrownBy(() -> sut.validateFamilyExists(familyId))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("validateFamilyExists 메서드는 id와 일치하는 Family를 조회하지 못하면 예외를 발생시켜야 한다.")
    void given_not_saved_id_when_validate_family_exists_then_throw_exception() {
        // given
        Long notSavedFamilyId = 999L;

        when(findFamilyPort.existsById(notSavedFamilyId))
            .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> sut.validateFamilyExists(notSavedFamilyId))
            .isInstanceOf(FTException.class)
            .hasMessageContaining("familyId");
    }

    @Test
    @DisplayName("validateFamilyAccess 메서드는 familyId와 userId를 전달받아 해당 user가 family 구성원인지 검증해야 한다.")
    void given_family_id_and_user_id_when_validate_family_access_then_validate_successfully() {
        // given
        Long familyId = 1L;
        Long userId = 2L;
        when(findFamilyMemberPort.existsByFamilyIdAndUserId(familyId, userId))
            .thenReturn(true);

        // when & then
        assertThatCode(() -> sut.validateFamilyAccess(familyId, userId))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateFamilyAccess 메서드는 familyId가 null이면 예외를 발생시켜야 한다.")
    void given_null_family_id_when_validate_family_access_then_throw_exception() {
        // given
        Long familyId = null;
        Long userId = 2L;

        // when & then
        assertThatThrownBy(() -> sut.validateFamilyAccess(familyId, userId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("familyId must not be null");
    }

    @Test
    @DisplayName("validateFamilyAccess 메서드는 userId가 null이면 예외를 발생시켜야 한다.")
    void given_null_user_id_when_validate_family_access_then_throw_exception() {
        // given
        Long familyId = 1L;
        Long userId = null;

        // when & then
        assertThatThrownBy(() -> sut.validateFamilyAccess(familyId, userId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userId must not be null");
    }

    @Test
    @DisplayName("validateFamilyAccess 메서드는 familyId, userId와 일치하는 FamilyMemer를 조회하지 못하면 예외를 발생시켜야 한다.")
    void given_invalid_familyId_and_invalid_user_id_when_validate_family_exists_then_throw_exception() {
        // given
        Long invalidFamilyId = 1L;
        Long invalidUserId = 2L;

        when(findFamilyMemberPort.existsByFamilyIdAndUserId(invalidFamilyId, invalidUserId))
            .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> sut.validateFamilyAccess(invalidFamilyId, invalidUserId))
            .isInstanceOf(FTException.class)
            .hasMessageContaining("familyId, userId");
    }
}
