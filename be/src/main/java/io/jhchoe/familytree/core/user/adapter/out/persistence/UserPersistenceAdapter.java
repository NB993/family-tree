package io.jhchoe.familytree.core.user.adapter.out.persistence;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 관련 아웃바운드 어댑터 구현
 */
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements FindUserPort {

    private final UserJpaRepository userJpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByNameContaining(String name, Pageable pageable) {
        return userJpaRepository.findByNameContaining(name, pageable)
            .stream()
            .map(UserJpaEntity::toUser)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return userJpaRepository.findById(id)
            .map(UserJpaEntity::toUser);
    }
}
