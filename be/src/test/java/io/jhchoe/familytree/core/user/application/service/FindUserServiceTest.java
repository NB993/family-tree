package io.jhchoe.familytree.core.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.jhchoe.familytree.core.user.application.port.in.FindUserByNameQuery;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[Unit Test] FindUserService")
@ExtendWith(MockitoExtension.class)
class FindUserServiceTest {

    @Mock
    private FindUserPort findUserPort;

    @InjectMocks
    private FindUserService sut;

    @Test
    @DisplayName("findById 메서드는 ID로 사용자를 조회해야 한다")
    void given_id_when_find_by_id_then_return_user() {
        // given
        Long userId = 1L;
        
        User expectedUser = User.withId(
            userId,
            "test@example.com",
            "Test User",
            "http://example.com/profile.jpg",
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        when(findUserPort.findById(userId)).thenReturn(Optional.of(expectedUser));

        // when
        Optional<User> result = sut.findById(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("findById 메서드는 존재하지 않는 ID로 조회 시 빈 Optional을 반환해야 한다")
    void given_non_existent_id_when_find_by_id_then_return_empty() {
        // given
        Long userId = 999L;
        
        when(findUserPort.findById(userId)).thenReturn(Optional.empty());

        // when
        Optional<User> result = sut.findById(userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById 메서드는 null id가 주어지면 예외를 발생시켜야 한다")
    void given_null_id_when_find_by_id_then_throw_exception() {
        // given
        Long userId = null;

        // when & then
        assertThatThrownBy(() -> sut.findById(userId))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("id must not be null");
    }

    @Test
    @DisplayName("findByName 메서드는 이름으로 사용자를 조회해야 한다")
    void given_name_when_find_by_name_then_return_users() {
        // given
        String name = "Test User";
        FindUserByNameQuery query = new FindUserByNameQuery(name);
        
        User user1 = User.withId(
            1L,
            "test1@example.com",
            name,
            null,
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        User user2 = User.withId(
            2L,
            "test2@example.com",
            name,
            null,
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        List<User> expectedUsers = Arrays.asList(user1, user2);
        
        when(findUserPort.findByName(name)).thenReturn(expectedUsers);

        // when
        List<User> result = sut.findByName(query);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedUsers);
    }

    @Test
    @DisplayName("findByName 메서드는 해당 이름의 사용자가 없을 경우 빈 리스트를 반환해야 한다")
    void given_non_existent_name_when_find_by_name_then_return_empty_list() {
        // given
        String name = "Non Existent Name";
        FindUserByNameQuery query = new FindUserByNameQuery(name);
        
        when(findUserPort.findByName(name)).thenReturn(Collections.emptyList());

        // when
        List<User> result = sut.findByName(query);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByName 메서드는 null 쿼리가 주어지면 예외를 발생시켜야 한다")
    void given_null_query_when_find_by_name_then_throw_exception() {
        // given
        FindUserByNameQuery query = null;

        // when & then
        assertThatThrownBy(() -> sut.findByName(query))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }

    @Test
    @DisplayName("findAll 메서드는 모든 사용자 목록을 반환해야 한다")
    void when_find_all_then_return_all_users() {
        // given
        User user1 = User.withId(
            1L,
            "user1@example.com",
            "User One",
            null,
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        User user2 = User.withId(
            2L,
            "user2@example.com",
            "User Two",
            null,
            1L,
            LocalDateTime.now().minusDays(1),
            null,
            null
        );
        
        List<User> expectedUsers = Arrays.asList(user1, user2);
        
        when(findUserPort.findAll()).thenReturn(expectedUsers);

        // when
        List<User> result = sut.findAll();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedUsers);
    }

    @Test
    @DisplayName("findAll 메서드는 사용자가 없을 경우 빈 리스트를 반환해야 한다")
    void given_no_users_when_find_all_then_return_empty_list() {
        // given
        when(findUserPort.findAll()).thenReturn(Collections.emptyList());

        // when
        List<User> result = sut.findAll();

        // then
        assertThat(result).isEmpty();
    }
}
