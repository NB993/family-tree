package io.jhchoe.familytree.common.auth.application.port.in;

import io.jhchoe.familytree.common.auth.domain.RefreshToken;
import java.util.List;
import java.util.Optional;

/**
 * Refresh Token 조회를 위한 유스케이스를 정의하는 인터페이스입니다.
 */
public interface FindRefreshTokenUseCase {

    /**
     * 사용자 ID로 Refresh Token을 조회합니다.
     * 사용자당 하나의 Refresh Token만 유지되므로 Optional로 반환합니다.
     *
     * @param query 사용자 ID를 포함하는 쿼리 객체
     * @return 조회된 Refresh Token (없으면 Optional.empty())
     */
    Optional<RefreshToken> find(FindRefreshTokenByUserIdQuery query);

    /**
     * 만료된 Refresh Token들을 조회합니다.
     * 배치 작업에서 만료된 토큰들을 정리할 때 사용됩니다.
     *
     * @param query 현재 시간을 포함하는 쿼리 객체
     * @return 만료된 Refresh Token 목록 (없으면 빈 리스트)
     */
    List<RefreshToken> findAll(FindExpiredRefreshTokensBeforeCurrentDateTimeQuery query);
}
