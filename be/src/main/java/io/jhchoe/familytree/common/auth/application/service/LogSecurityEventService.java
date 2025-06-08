package io.jhchoe.familytree.common.auth.application.service;

import io.jhchoe.familytree.common.auth.application.port.in.LogSecurityEventCommand;
import io.jhchoe.familytree.common.auth.application.port.in.LogSecurityEventUseCase;
import io.jhchoe.familytree.common.auth.application.port.out.SaveSecurityEventPort;
import io.jhchoe.familytree.common.auth.domain.SecurityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 보안 이벤트 로깅 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogSecurityEventService implements LogSecurityEventUseCase {

    private final SaveSecurityEventPort saveSecurityEventPort;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long logEvent(final LogSecurityEventCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // 보안 이벤트 도메인 객체 생성
        final SecurityEvent securityEvent = SecurityEvent.newEvent(
            command.eventType(),
            command.userId(),
            command.ipAddress(),
            command.userAgent(),
            command.description()
        );

        // 보안 이벤트 저장
        final Long savedEventId = saveSecurityEventPort.save(securityEvent);

        // 보안 이벤트 로깅 (개발자를 위한 추가 로그)
        log.warn("Security Event Logged: {} - IP: {} - User: {} - Description: {}", 
            command.eventType(), 
            command.ipAddress(), 
            command.userId(), 
            command.description()
        );

        return savedEventId;
    }
}
