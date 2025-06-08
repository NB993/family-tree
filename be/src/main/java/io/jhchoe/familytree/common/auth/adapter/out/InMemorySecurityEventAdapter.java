package io.jhchoe.familytree.common.auth.adapter.out;

import io.jhchoe.familytree.common.auth.application.port.out.SaveSecurityEventPort;
import io.jhchoe.familytree.common.auth.domain.SecurityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 메모리 기반 보안 이벤트 저장 구현체입니다.
 * 개발/테스트 환경에서 사용하며, 프로덕션에서는 데이터베이스 기반 구현체로 교체해야 합니다.
 */
@Slf4j
@Component
public class InMemorySecurityEventAdapter implements SaveSecurityEventPort {

    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Long save(SecurityEvent securityEvent) {
        if (securityEvent == null) {
            log.warn("Security event is null, skipping save");
            return null;
        }

        final Long eventId = idGenerator.getAndIncrement();
        
        log.info("Security event saved: [ID: {}] [Type: {}] [User: {}] [IP: {}] [Details: {}]",
            eventId,
            securityEvent.getEventType(),
            securityEvent.getUserId(),
            securityEvent.getIpAddress(),
            securityEvent.getDescription()
        );

        return eventId;
    }
}
