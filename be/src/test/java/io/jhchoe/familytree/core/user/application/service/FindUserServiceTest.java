package io.jhchoe.familytree.core.user.application.service;

import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.user.application.port.in.FindUserByNameQuery;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("[Unit Test] FindUserService")
@ExtendWith(MockitoExtension.class)
class FindUserServiceTest {

    @Mock
    private FindUserPort findUserPort;

    @InjectMocks
    private FindUserService sut;

    @Test
    @DisplayName("이름으로 사용자 조회 시 결과가 있으면 사용자 목록을 반환한다")
    void given_valid_name_when_find_by_name_then_return_user_list() {
        // given
        String name = "홍길동";
        int page = 0;
        int size = 10;
        FindUserByNameQuery query = new FindUserByNameQuery(name, page, size);
        
        User user = User.withId(1L, "test@example.com", "홍길동", "profile.jpg", null,
                AuthenticationType.OAUTH2, OAuth2Provider.GOOGLE, UserRole.USER, false, 2L, LocalDateTime.now(), 2L, LocalDateTime.now(), null);
        List<User> users = List.of(user);
        
        when(findUserPort.findByNameContaining(eq(name), any())).thenReturn(users);

        // when
        List<User> result = sut.findByName(query);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(user);
    }
    
    @Test
    @DisplayName("이름으로 사용자 조회 시 결과가 없으면 예외를 발생시킨다")
    void given_invalid_name_when_find_by_name_then_throw_exception() {
        // given
        String name = "존재하지않는이름";
        int page = 0;
        int size = 10;
        FindUserByNameQuery query = new FindUserByNameQuery(name, page, size);
        
        when(findUserPort.findByNameContaining(eq(name), any())).thenReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> sut.findByName(query))
            .isInstanceOf(FTException.class);
    }

    @Test
    @DisplayName("쿼리 객체가 null이면 NullPointerException을 발생시킨다")
    void given_null_query_when_find_by_name_then_throw_null_pointer_exception() {
        // given
        FindUserByNameQuery query = null;

        // when & then
        assertThatThrownBy(() -> sut.findByName(query))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("query must not be null");
    }
}
