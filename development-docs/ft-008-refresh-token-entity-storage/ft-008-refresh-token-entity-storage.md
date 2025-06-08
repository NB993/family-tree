# FT-008: RefreshToken 엔티티 및 토큰 저장소 구현 완료

## 문서 정보
- **Story ID**: FT-008
- **Story 제목**: RefreshToken 엔티티 및 토큰 저장소 구현
- **구현 일자**: 2025-06-08
- **구현자**: AI 개발자

---

## 구현 개요
JWT 인증 시스템의 핵심 구성요소인 RefreshToken 엔티티와 저장소를 구현했습니다. 이는 Access Token 갱신을 위한 장기 토큰 관리 시스템의 기반이 됩니다.

## 구현된 주요 컴포넌트

### 도메인 객체
- **RefreshToken**: JWT 리프레시 토큰을 위한 도메인 엔티티
  - 사용자별 고유 토큰 관리
  - 토큰 만료 검증 로직 포함
  - 불변 객체로 설계하여 데이터 무결성 보장

### 애플리케이션 계층

#### Command/Query 객체
- **FindRefreshTokenByUserIdQuery**: 사용자 ID 기반 토큰 조회
- **FindExpiredRefreshTokensQuery**: 만료된 토큰 일괄 조회 (배치 작업용)
- **SaveRefreshTokenCommand**: 토큰 저장 및 갱신
- **DeleteRefreshTokenCommand**: 토큰 삭제 (로그아웃 시)

#### UseCase 인터페이스
- **FindRefreshTokenUseCase**: 토큰 조회 유스케이스
- **SaveRefreshTokenUseCase**: 토큰 저장 유스케이스  
- **DeleteRefreshTokenUseCase**: 토큰 삭제 유스케이스

#### Service 클래스
- **FindRefreshTokenService**: 토큰 조회 비즈니스 로직
- **SaveRefreshTokenService**: 토큰 저장 비즈니스 로직
- **DeleteRefreshTokenService**: 토큰 삭제 비즈니스 로직

#### 아웃바운드 포트
- **FindRefreshTokenPort**: 토큰 조회 포트
- **SaveRefreshTokenPort**: 토큰 저장 포트
- **DeleteRefreshTokenPort**: 토큰 삭제 포트

### 인프라 계층
- **RefreshTokenJpaEntity**: JPA 엔티티 (refresh_tokens 테이블)
- **RefreshTokenJpaRepository**: Spring Data JPA 리포지토리
- **RefreshTokenAdapter**: 아웃바운드 포트 구현체

## 데이터베이스 스키마

### refresh_tokens 테이블
```sql
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_expires_at (expires_at)
);
```

### 주요 설계 특징
- **사용자당 하나의 토큰**: `user_id`에 UNIQUE 제약조건으로 중복 방지
- **만료 검색 최적화**: `expires_at` 인덱스로 배치 작업 성능 향상
- **자동 타임스탬프**: JPA `@PrePersist`, `@PreUpdate`로 시간 관리

## 핵심 비즈니스 로직

### 토큰 Upsert 동작
```java
// 기존 토큰이 있다면 삭제 후 새로 저장 (Upsert 동작)
if (refreshToken.getId() == null && refreshTokenJpaRepository.existsByUserId(refreshToken.getUserId())) {
    refreshTokenJpaRepository.deleteByUserId(refreshToken.getUserId());
    refreshTokenJpaRepository.flush(); // 즉시 삭제 반영
}
```

### 토큰 만료 검증
```java
public boolean isExpired(LocalDateTime currentDateTime) {
    Objects.requireNonNull(currentDateTime, "currentDateTime must not be null");
    return expiresAt.isBefore(currentDateTime);
}
```

### 배치 작업 지원
```java
// 만료된 토큰 조회
@Query("SELECT rt FROM RefreshTokenJpaEntity rt WHERE rt.expiresAt < :currentDateTime")
List<RefreshTokenJpaEntity> findExpiredTokens(@Param("currentDateTime") LocalDateTime currentDateTime);

// 만료된 토큰 일괄 삭제
@Modifying
@Query("DELETE FROM RefreshTokenJpaEntity rt WHERE rt.expiresAt < :currentDateTime")
int deleteExpiredTokens(@Param("currentDateTime") LocalDateTime currentDateTime);
```

## 테스트 구현

### 단위 테스트 (11개 테스트)
- **Command/Query 객체 테스트**: 유효성 검증 로직 테스트
- **Service 테스트**: 비즈니스 로직 및 포트 호출 검증
- **Adapter 테스트**: 데이터베이스 연동 및 변환 로직 검증

### 테스트 커버리지
- ✅ 정상 케이스: 토큰 생성, 조회, 삭제
- ✅ 예외 케이스: null 파라미터, 존재하지 않는 사용자
- ✅ 경계 케이스: 만료된 토큰, 중복 토큰 처리
- ✅ 비즈니스 규칙: 사용자당 하나의 토큰, Upsert 동작

### 주요 테스트 케이스
```java
@Test
@DisplayName("기존 사용자 토큰이 있을 때 새 토큰 저장 시 기존 토큰이 교체됩니다")
void save_replaces_existing_token_when_user_already_has_token() {
    // 사용자당 하나의 토큰만 유지되는지 검증
}

@Test  
@DisplayName("만료된 토큰들을 조회 시 토큰 목록을 반환합니다")
void find_expired_tokens_returns_expired_tokens_only() {
    // 배치 작업을 위한 만료 토큰 필터링 검증
}
```

## 보안 고려사항

### 토큰 보안
- **토큰 해시 저장**: 원본 토큰 대신 해시값만 DB에 저장
- **토큰 정보 보호**: toString()에서 토큰 해시를 `[PROTECTED]`로 마스킹
- **자동 만료**: 만료 시간 기반 토큰 유효성 검증

### 데이터 보안
- **사용자 격리**: 사용자별 토큰 접근 제한
- **자동 정리**: 만료된 토큰 배치 삭제로 저장소 정리

## 성능 최적화

### 데이터베이스 최적화
- **인덱스 활용**: `user_id` UNIQUE 인덱스로 빠른 조회
- **만료 토큰 인덱스**: `expires_at` 인덱스로 배치 작업 최적화
- **즉시 삭제**: `flush()`로 Upsert 시 일관성 보장

### 메모리 최적화
- **불변 객체**: RefreshToken 도메인 객체 불변성으로 안전성 확보
- **Optional 활용**: null 대신 Optional로 명시적 부재 표현

## 헥사고날 아키텍처 준수

### 포트와 어댑터 분리
- **인바운드 포트**: UseCase 인터페이스로 애플리케이션 진입점 정의
- **아웃바운드 포트**: 외부 저장소 추상화로 구현 세부사항 분리
- **어댑터**: JPA 구현체로 포트 인터페이스 구현

### 의존성 역전
- **코어 → 인프라**: 도메인이 인프라에 의존하지 않음
- **포트 인터페이스**: 코어 계층에서 정의, 인프라에서 구현
- **의존성 주입**: Spring Framework로 런타임 의존성 해결

## 알려진 이슈 및 제약사항

### 현재 제약사항
- **단일 서버 환경**: 현재는 단일 서버 환경을 가정
- **토큰 갱신 미구현**: 토큰 갱신 API는 FT-011에서 구현 예정
- **배치 스케줄링 미포함**: 만료 토큰 정리 스케줄러는 별도 구현 필요

### 향후 개선사항
- **분산 환경 지원**: Redis 기반 토큰 저장소 고려
- **토큰 블랙리스트**: 강제 로그아웃을 위한 토큰 무효화 기능
- **감사 로그**: 토큰 생성/삭제 이벤트 로깅

## 다음 단계 (FT-009)

### 연계 작업
- **JWT 인증 필터**: Spring Security와 RefreshToken 통합
- **토큰 검증 로직**: Access Token 검증 시 RefreshToken 활용
- **토큰 갱신 플로우**: 만료된 Access Token 갱신 프로세스

### 의존성
- **FT-007 완료**: JWT 토큰 유틸리티 (✅ 완료)
- **FT-008 완료**: RefreshToken 저장소 (✅ 완료)
- **FT-009 진행**: JWT 인증 필터 구현

## 시니어 개발자 관점의 추가 설명

### 아키텍처 설계 고려사항
RefreshToken 저장소는 JWT 인증 시스템의 핵심 구성요소로, 다음과 같은 설계 원칙을 적용했습니다:

1. **단일 책임 원칙**: 각 Service는 하나의 UseCase만 구현
2. **개방-폐쇄 원칙**: 포트 인터페이스를 통해 확장에는 열려있고 수정에는 닫혀있음
3. **의존성 역전 원칙**: 고수준 모듈이 저수준 모듈에 의존하지 않음

### 확장성 고려사항
- **멀티 서버 환경**: Redis 기반 저장소로 교체 시 포트 인터페이스만 변경하면 됨
- **성능 스케일링**: 읽기 전용 복제본 활용 시 Read/Write 포트 분리 가능
- **보안 강화**: 토큰 암호화, 사용자 행동 분석 등 추가 기능 확장 용이

### 유지보수 시 주의사항
- **사용자당 단일 토큰**: 비즈니스 규칙 변경 시 스키마 및 로직 수정 필요
- **배치 작업**: 대용량 만료 토큰 처리 시 성능 모니터링 필요
- **트랜잭션 경계**: Upsert 동작의 일관성을 위해 트랜잭션 범위 유지

---

## 구현 완료 체크리스트

- ✅ **RefreshToken 도메인 엔티티 구현**
- ✅ **Command/Query 객체 4개 구현**
- ✅ **UseCase 인터페이스 3개 구현**
- ✅ **Service 클래스 3개 구현**
- ✅ **아웃바운드 포트 3개 구현**
- ✅ **JPA 엔티티 및 리포지토리 구현**
- ✅ **통합 어댑터 구현**
- ✅ **단위 테스트 11개 구현 및 통과**
- ✅ **전체 테스트 통과 (546개 테스트)**

**FT-008 RefreshToken 엔티티 및 토큰 저장소 구현이 성공적으로 완료되었습니다!** 🎉
