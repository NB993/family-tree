package io.jhchoe.familytree.core.user.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserRepository;
import io.jhchoe.familytree.common.auth.domain.AuthenticationType;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import io.jhchoe.familytree.core.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("[Integration Test] UserAdapter")
class UserAdapterTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserAdapter userAdapter(UserRepository userRepository) {
            return new UserAdapter(userRepository);
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAdapter sut;

    @Test
    @DisplayName("findById 메서드는 ID로 사용자를 조회해야 한다")
    void given_id_when_find_by_id_then_return_user() {
        // given
        UserJpaEntity userEntity = UserJpaEntity.ofFormLoginUser(
            "test@example.com",
            "password"
        );
        userEntity = userRepository.save(userEntity);

        // when
        Optional<User> result = sut.findById(userEntity.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userEntity.getId());
        assertThat(result.get().getEmail()).isEqualTo(userEntity.getEmail());
    }

    @Test
    @DisplayName("findById 메서드는 존재하지 않는 ID로 조회 시 빈 Optional을 반환해야 한다")
    void given_non_existent_id_when_find_by_id_then_return_empty() {
        // given
        Long nonExistentId = 999L;

        // when
        Optional<User> result = sut.findById(nonExistentId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByName 메서드는 이름으로 사용자를 조회해야 한다")
    void given_name_when_find_by_name_then_return_users() {
        // given
        String name = "Test User";
        
        UserJpaEntity userEntity1 = UserJpaEntity.ofOAuth2User(
            "test1@example.com",
            name,
            "http://example.com/profile1.jpg",
            OAuth2Provider.GOOGLE
        );
        
        UserJpaEntity userEntity2 = UserJpaEntity.ofOAuth2User(
            "test2@example.com",
            name,
            "http://example.com/profile2.jpg",
            OAuth2Provider.GOOGLE
        );
        
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);

        // when
        List<User> result = sut.findByName(name);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName).containsOnly(name);
    }

    @Test
    @DisplayName("findByName 메서드는 해당 이름의 사용자가 없을 경우 빈 리스트를 반환해야 한다")
    void given_non_existent_name_when_find_by_name_then_return_empty_list() {
        // given
        String nonExistentName = "Non Existent Name";

        // when
        List<User> result = sut.findByName(nonExistentName);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll 메서드는 모든 사용자 목록을 반환해야 한다")
    void when_find_all_then_return_all_users() {
        // given
        userRepository.deleteAll(); // 테스트 시작 전 모든 데이터 삭제
        
        UserJpaEntity userEntity1 = UserJpaEntity.ofFormLoginUser(
            "user1@example.com",
            "password1"
        );
        
        UserJpaEntity userEntity2 = UserJpaEntity.ofFormLoginUser(
            "user2@example.com",
            "password2"
        );
        
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);

        // when
        List<User> result = sut.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getEmail)
            .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    @DisplayName("findAll 메서드는 사용자가 없을 경우 빈 리스트를 반환해야 한다")
    void given_no_users_when_find_all_then_return_empty_list() {
        // given
        userRepository.deleteAll();

        // when
        List<User> result = sut.findAll();

        // then
        assertThat(result).isEmpty();
    }
}
