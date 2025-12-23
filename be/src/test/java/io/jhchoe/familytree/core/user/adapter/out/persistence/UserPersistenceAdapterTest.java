package io.jhchoe.familytree.core.user.adapter.out.persistence;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.common.auth.domain.UserRole;
import io.jhchoe.familytree.core.user.domain.User;
import io.jhchoe.familytree.helper.TestcontainersDataJpaTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Adapter Test] UserPersistenceAdapter")
@Import(UserPersistenceAdapter.class)
class UserPersistenceAdapterTest extends TestcontainersDataJpaTestBase {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserPersistenceAdapter sut;

    @Test
    @DisplayName("이름 검색 시 포함된 이름을 가진 사용자 목록을 반환한다")
    void given_name_substring_when_find_by_name_containing_then_return_matching_users() {
        // given
        User user1 = User.newUser("test1@example.com", "홍길동", "profile1.jpg", null, AuthenticationType.OAUTH2, OAuth2Provider.GOOGLE, UserRole.USER, false);
        User user2 = User.newUser("test2@example.com", "홍길순", "profile2.jpg", null, AuthenticationType.OAUTH2, OAuth2Provider.GOOGLE, UserRole.USER, false);
        User user3 = User.newUser("test3@example.com", "김철수", "profile3.jpg", null, AuthenticationType.OAUTH2, OAuth2Provider.GOOGLE, UserRole.USER, false);

        entityManager.persist(UserJpaEntity.ofOAuth2User(user1));
        entityManager.persist(UserJpaEntity.ofOAuth2User(user2));
        entityManager.persist(UserJpaEntity.ofOAuth2User(user3));
        entityManager.flush();
        
        String nameSubstring = "홍길";
        Pageable pageable = PageRequest.of(0, 10);
        
        // when
        List<User> result = sut.findByNameContaining(nameSubstring, pageable);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName)
            .containsExactlyInAnyOrder("홍길동", "홍길순");
    }
    
    @Test
    @DisplayName("이름 검색 시 결과가 없으면 빈 목록을 반환한다")
    void given_non_existent_name_when_find_by_name_containing_then_return_empty_list() {
        // given
        User user = User.newUser("test@example.com", "홍길동", "profile.jpg", null, AuthenticationType.OAUTH2, OAuth2Provider.GOOGLE, UserRole.USER, false);
        entityManager.persist(UserJpaEntity.ofOAuth2User(user));
        entityManager.flush();

        String nameSubstring = "존재하지않는이름";
        Pageable pageable = PageRequest.of(0, 10);
        
        // when
        List<User> result = sut.findByNameContaining(nameSubstring, pageable);
        
        // then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("페이징 처리가 정상적으로 동작한다")
    void given_multiple_users_when_find_with_pagination_then_return_paged_results() {
        // given
        for (int i = 1; i <= 5; i++) {
            User user = User.newUser(
                "test" + i + "@example.com",
                "홍길동" + i,
                "profile" + i + ".jpg",
                null, // kakaoId
                AuthenticationType.OAUTH2,
                OAuth2Provider.GOOGLE,
                UserRole.USER,
                false
            );
            entityManager.persist(UserJpaEntity.ofOAuth2User(user));
        }
        entityManager.flush();
        
        String nameSubstring = "홍길동";
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);
        
        // when
        List<User> firstPageResults = sut.findByNameContaining(nameSubstring, firstPage);
        List<User> secondPageResults = sut.findByNameContaining(nameSubstring, secondPage);
        
        // then
        assertThat(firstPageResults).hasSize(2);
        assertThat(secondPageResults).hasSize(2);
        
        // 첫 페이지와 두 번째 페이지의 결과가 다른지 확인
        List<String> firstPageNames = firstPageResults.stream()
            .map(User::getName)
            .toList();
        List<String> secondPageNames = secondPageResults.stream()
            .map(User::getName)
            .toList();
        
        assertThat(firstPageNames).doesNotContainAnyElementsOf(secondPageNames);
    }
}
