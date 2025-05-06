package io.jhchoe.familytree.core.user.application.service;

import io.jhchoe.familytree.core.user.application.port.in.FindUserByNameQuery;
import io.jhchoe.familytree.core.user.application.port.in.FindUserUseCase;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 조회 기능을 구현하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindUserService implements FindUserUseCase {

    private final FindUserPort findUserPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return findUserPort.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> findByName(FindUserByNameQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        return findUserPort.findByName(query.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return findUserPort.findAll();
    }
}
