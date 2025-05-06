package io.jhchoe.familytree.core.user.application.service;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import io.jhchoe.familytree.core.user.application.port.in.FindUserByNameQuery;
import io.jhchoe.familytree.core.user.application.port.in.FindUserUseCase;
import io.jhchoe.familytree.core.user.application.port.out.FindUserPort;
import io.jhchoe.familytree.core.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 사용자 조회 관련 비즈니스 로직을 처리하는 서비스
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
    public List<User> findByName(FindUserByNameQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        List<User> users = findUserPort.findByNameContaining(query.getName(), pageable);
        
        // 검색 결과가 없으면 예외 발생
        if (users.isEmpty()) {
            throw new FTException(CommonExceptionCode.NOT_FOUND, "user");
        }
        
        return users;
    }
}
