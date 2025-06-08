package io.jhchoe.familytree.common.auth.application.port.out;

import io.jhchoe.familytree.common.auth.domain.SecurityEvent;

/**
 * 보안 이벤트 저장을 위한 아웃바운드 포트입니다.
 */
public interface SaveSecurityEventPort {

    /**
     * 보안 이벤트를 저장합니다.
     *
     * @param securityEvent 저장할 보안 이벤트
     * @return 저장된 보안 이벤트의 ID
     */
    Long save(SecurityEvent securityEvent);
}
