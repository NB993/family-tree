package io.jhchoe.familytree.core.family.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.family.application.port.in.ModifyFamilyCommand;
import io.jhchoe.familytree.core.family.application.port.out.FindFamilyPort;
import io.jhchoe.familytree.core.family.application.port.out.ModifyFamilyPort;
import io.jhchoe.familytree.core.family.domain.Family;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] ModifyFamilyService")
@ExtendWith(MockitoExtension.class)
class ModifyFamilyServiceTest {

    @Mock
    private FindFamilyPort findFamilyPort;

    @Mock
    private ModifyFamilyPort modifyFamilyPort;

    @InjectMocks
    private ModifyFamilyService sut;

    @Test
    @DisplayName("modify 메서드는 유효한 ModifyFamilyCommand를 받고 Family를 수정한 후 ID를 반환해야 한다.")
    void given_valid_command_when_modify_then_modify_family_and_return_id() {
        // given
        Long familyId = 1L;
        ModifyFamilyCommand command = new ModifyFamilyCommand(familyId, "Updated Name", "http://example.com/profile",
            "Updated Description");

        Family family = Family.withId(familyId, "Old Name", "Old Description", "http://example.com/old-profile", null,
            null, null, null);

        when(findFamilyPort.findById(familyId)).thenReturn(Optional.of(family));
        when(modifyFamilyPort.modifyFamily(family)).thenReturn(familyId);

        // when
        Long result = sut.modify(command);

        // then
        assertThat(result).isEqualTo(familyId);
    }

    @Test
    @DisplayName("modify 메서드는 ModifyFamilyCommand가 null일 경우 예외를 발생시켜야 한다.")
    void given_null_command_when_modify_then_throw_exception() {
        // given
        ModifyFamilyCommand command = null;

        // when & then
        assertThatThrownBy(() -> sut.modify(command))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("command must not be null");
    }

    @Test
    @DisplayName("modify 메서드는 loadFamilyPort에서 Family를 찾을 수 없는 경우 FTException을 발생시켜야 한다.")
    void given_nonexistent_family_when_modify_then_throw_null_pointer_exception() {
        // given
        Long familyId = 1L;
        ModifyFamilyCommand command = new ModifyFamilyCommand(familyId, "Updated Name", "http://example.com/profile",
            "Updated Description");

        when(findFamilyPort.findById(familyId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sut.modify(command))
            .isInstanceOf(FTException.class);
    }

    @Test
    @DisplayName("modify 메서드는 ModifyFamilyPort가 실패하면 예외를 발생시켜야 한다.")
    void given_failing_modify_family_port_when_modify_then_throw_exception() {
        // given
        Long familyId = 1L;
        ModifyFamilyCommand command = new ModifyFamilyCommand(familyId, "Updated Name", "http://example.com/profile",
            "Updated Description");
        Family family = Family.withId(familyId, "Old Name", "Old Description", "http://example.com/old-profile", null,
            null, null, null);

        when(findFamilyPort.findById(familyId)).thenReturn(Optional.of(family));
        when(modifyFamilyPort.modifyFamily(any(Family.class))).thenThrow(RuntimeException.class);

        // when & then
        assertThatThrownBy(() -> sut.modify(command))
            .isInstanceOf(RuntimeException.class);
    }
}
