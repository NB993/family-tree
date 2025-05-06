package io.jhchoe.familytree.core.user.adapter.out.persistence;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserRepository;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 사용자 조회를 위한 영속성 어댑터 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class UserAdapter implements FindUserPort {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
            .map(this::mapToUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByName(String name) {
        return userRepository.findAll().stream()
            .filter(entity -> name.equals(entity.getName()))
            .map(this::mapToUser)
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
            .map(this::mapToUser)
            .collect(Collectors.toList());
    }

    /**
     * UserJpaEntity를 User 도메인 객체로 변환합니다.
     *
     * @param entity 변환할 UserJpaEntity
     * @return 변환된 User 도메인 객체
     */
    private User mapToUser(UserJpaEntity entity) {
        return User.withId(
            entity.getId(),
            entity.getEmail(),
            entity.getName(),
            entity.getProfileUrl(),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getModifiedBy(),
            entity.getModifiedAt()
        );
    }
}
