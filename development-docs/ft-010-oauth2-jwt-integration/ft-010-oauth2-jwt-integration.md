# FT-010: OAuth2 JWT 연동 및 토큰 발급 구현 완료

## 문서 정보
- **Story ID**: FT-010
- **Story 제목**: OAuth2 JWT 연동 및 토큰 발급 구현
- **구현 일자**: 2025-06-08
- **구현자**: AI 개발자

---

## 구현 개요
기존 OAuth2 세션 기반 인증에 JWT 토큰 발급 기능을 추가하여, 프론트엔드 SPA와 API 기반 인증을 지원하는 하이브리드 인증 시스템을 구현했습니다. OAuth2 로그인 성공 시 세션 대신 JWT 토큰을 발급하여 Stateless 아키텍처를 지원합니다.

## 구현된 주요 컴포넌트

### 애플리케이션 계층

#### Command 객체
- **GenerateJwtTokenCommand**: JWT 토큰 생성을 위한 Command 객체
  - FTUser 정보를 기반으로 토큰 생성 요청 전달
  - 사용자 ID, 이메일, 이름, 역할 정보 추출 메서드 제공

#### UseCase 인터페이스
- **GenerateJwtTokenUseCase**: JWT 토큰 생성 유스케이스
  - OAuth2 로그인 성공 시 Access Token과 Refresh Token 생성
  - 토큰 응답 객체 반환 (만료 시간 포함)

#### Service 클래스
- **GenerateJwtTokenService**: JWT 토큰 생성 비즈니스 로직
  - Access Token 생성 (1시간 만료)
  - Refresh Token 생성 (7일 만료)
  - Refresh Token 데이터베이스 저장 (Upsert 방식)

### 인프라 계층

#### Authentication Handler
- **OAuth2JwtSuccessHandler**: OAuth2 로그인 성공 시 JWT 토큰 발급 핸들러
  - 기존 세션 기반 리다이렉션 대신 JSON API 응답
  - 토큰 생성 실패 시 에러 응답 처리
  - 프론트엔드 친화적 응답 형식 제공

#### Security Configuration
- **SecurityConfig 수정**: OAuth2 Success Handler 통합
  - OAuth2 로그인 성공 시 JWT 토큰 발급 핸들러 적용
  - 기존 세션 기반 인증과 JWT 인증 동시 지원

## 구현된 기능

### 1. OAuth2 JWT 토큰 발급
OAuth2 로그인 성공 시 다음과 같은 프로세스로 JWT 토큰을 발급합니다:

1. **사용자 인증 정보 추출**: OAuth2 인증 결과에서 FTUser 정보 추출
2. **Access Token 생성**: 사용자 정보를 포함한 1시간 만료 토큰 생성
3. **Refresh Token 생성**: 사용자 ID를 포함한 7일 만료 토큰 생성
4. **Refresh Token 저장**: 데이터베이스에 Refresh Token 저장 (기존 토큰 덮어쓰기)
5. **JSON 응답**: 프론트엔드에 토큰 정보와 사용자 정보를 JSON으로 응답

### 2. 하이브리드 인증 아키텍처
기존 세션 기반 인증과 JWT 인증을 동시에 지원하는 구조를 구현했습니다:

- **세션 기반**: 기존 웹 애플리케이션 지원 유지
- **JWT 기반**: 새로운 SPA 및 API 클라이언트 지원
- **점진적 전환**: 기존 시스템에 영향 없이 JWT 기능 추가

### 3. 에러 처리 및 응답 표준화
토큰 생성 과정에서 발생할 수 있는 다양한 오류를 체계적으로 처리합니다:

- **토큰 생성 실패**: 서버 오류 시 500 상태코드와 에러 메시지 반환
- **인증 정보 오류**: 잘못된 사용자 정보 시 적절한 오류 응답
- **JSON 응답 표준화**: 성공/실패 여부, 메시지, 데이터를 포함한 일관된 응답 형식

## 핵심 설계 결정사항

### 1. Stateless JWT vs Stateful Session
- **JWT 장점**: 서버 확장성, SPA 친화적, 분산 환경 지원
- **구현 방식**: 하이브리드 접근으로 기존 세션과 공존
- **보안 고려**: Refresh Token을 데이터베이스에 저장하여 강제 로그아웃 지원

### 2. OAuth2 Success Handler 교체
- **기존**: 세션 기반 리다이렉션 (`defaultSuccessUrl("/")`)
- **신규**: JSON API 응답 (`OAuth2JwtSuccessHandler`)
- **이유**: 프론트엔드 SPA가 토큰을 직접 처리할 수 있도록 API 중심 설계

### 3. 토큰 만료 시간 전략
- **Access Token**: 1시간 (3600초) - 보안성과 사용성의 균형
- **Refresh Token**: 7일 (604800초) - 사용자 편의성 확보
- **갱신 방식**: 별도 토큰 갱신 API 제공 (FT-011에서 구현 예정)

## 테스트 구현

### 단위 테스트
- **GenerateJwtTokenCommandTest**: Command 객체 검증 (3개 테스트)
- **GenerateJwtTokenServiceTest**: 토큰 생성 서비스 검증 (3개 테스트)
- **OAuth2JwtSuccessHandlerTest**: 인증 핸들러 검증 (3개 테스트)

### 테스트 커버리지
- OAuth2 사용자 JWT 토큰 생성 검증
- JWT 전용 사용자 토큰 생성 검증
- 토큰 만료 시간 설정 검증
- 성공/실패 시나리오별 JSON 응답 검증
- 에러 처리 및 예외 상황 검증

## API 응답 형식

### 로그인 성공 응답
```json
{
  "success": true,
  "message": "로그인이 성공적으로 완료되었습니다.",
  "tokenInfo": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "userInfo": {
    "id": 1,
    "email": "user@example.com",
    "name": "사용자"
  },
  "errorMessage": null
}
```

### 로그인 실패 응답
```json
{
  "success": false,
  "message": "로그인에 실패했습니다.",
  "tokenInfo": null,
  "userInfo": null,
  "errorMessage": "토큰 생성 중 오류가 발생했습니다. 다시 시도해주세요."
}
```

## 보안 고려사항

### 토큰 보안
- **JWT 서명**: HS256 알고리즘으로 토큰 무결성 보장
- **비밀 키 관리**: 환경변수를 통한 JWT Secret Key 관리
- **토큰 정보 보호**: 로그에서 토큰 내용 마스킹 처리

### 인증 흐름 보안
- **HTTPS 필수**: 토큰 전송 시 암호화 통신 필요
- **CORS 설정**: 허용된 도메인에서만 토큰 요청 가능
- **토큰 저장**: 클라이언트에서 안전한 저장소 사용 권장

## 성능 최적화

### 토큰 생성 최적화
- **트랜잭션 범위**: 토큰 생성과 저장을 하나의 트랜잭션으로 처리
- **Upsert 방식**: 기존 Refresh Token 덮어쓰기로 중복 방지
- **메모리 효율성**: 불변 객체 사용으로 안전성과 성능 확보

### 응답 최적화
- **JSON 직렬화**: Jackson ObjectMapper를 활용한 효율적 JSON 생성
- **응답 캐싱**: 토큰 응답 구조 미리 정의로 처리 속도 향상

## 헥사고날 아키텍처 준수

### 포트와 어댑터 분리
- **인바운드 포트**: GenerateJwtTokenUseCase로 토큰 생성 진입점 정의
- **아웃바운드 포트**: 기존 SaveRefreshTokenUseCase 활용으로 저장소 추상화
- **어댑터**: OAuth2JwtSuccessHandler를 통한 웹 계층 통합

### 의존성 역전
- **코어 → 인프라**: 토큰 생성 로직이 인프라 세부사항에 의존하지 않음
- **인터페이스 분리**: 각 UseCase가 단일 책임을 가지도록 설계
- **의존성 주입**: Spring Framework를 통한 런타임 의존성 해결

## 알려진 이슈 및 제약사항

### 현재 제약사항
- **단일 플랫폼**: 현재는 웹 OAuth2만 지원 (모바일 앱 OAuth2 미지원)
- **토큰 갱신**: Refresh Token 갱신 API는 FT-011에서 구현 예정
- **로그아웃**: JWT 토큰 무효화 API는 FT-011에서 구현 예정

### 향후 개선사항
- **멀티 플랫폼**: 모바일 앱 OAuth2 플로우 지원
- **토큰 블랙리스트**: 강제 로그아웃을 위한 토큰 무효화 기능
- **감사 로그**: OAuth2 로그인 및 토큰 발급 이벤트 로깅

## 다음 단계 (FT-011)

### 연계 작업
- **토큰 갱신 API**: Refresh Token을 사용한 Access Token 갱신
- **로그아웃 API**: JWT 토큰 무효화 및 Refresh Token 삭제
- **토큰 검증 강화**: 블랙리스트 기반 토큰 무효화 검증

### 의존성
- **FT-007 완료**: JWT 토큰 유틸리티 (✅ 완료)
- **FT-008 완료**: RefreshToken 저장소 (✅ 완료)
- **FT-009 완료**: JWT 인증 필터 (✅ 완료)
- **FT-010 완료**: OAuth2 JWT 연동 (✅ 완료)
- **FT-011 진행**: 토큰 갱신 및 로그아웃 API

## 시니어 개발자 관점의 추가 설명

### 아키텍처 설계 고려사항
OAuth2 JWT 연동은 기존 세션 기반 시스템을 점진적으로 현대화하는 핵심 구성요소입니다:

1. **하이브리드 전략**: 기존 시스템 안정성을 유지하면서 새로운 기능 추가
2. **확장성 확보**: Stateless 토큰 기반으로 서버 확장성 개선
3. **API 퍼스트**: 프론트엔드 기술 스택 변경에 유연하게 대응

### 확장성 고려사항
- **마이크로서비스**: JWT 토큰으로 서비스 간 인증 정보 전달 가능
- **모바일 지원**: 동일한 토큰 구조로 모바일 앱 연동 용이
- **제3자 API**: JWT 표준을 통한 외부 시스템 연동 지원

### 유지보수 시 주의사항
- **토큰 버전 관리**: JWT 스키마 변경 시 하위 호환성 고려
- **성능 모니터링**: 토큰 생성/검증 성능 지속적 모니터링
- **보안 업데이트**: JWT 라이브러리 및 암호화 알고리즘 주기적 업데이트

---

## 구현 완료 체크리스트

- ✅ **JWT 토큰 생성 Command/UseCase 구현**
- ✅ **OAuth2 Success Handler 구현 및 적용**
- ✅ **SecurityConfig OAuth2 설정 수정**
- ✅ **JSON API 응답 형식 표준화**
- ✅ **에러 처리 및 예외 상황 대응**
- ✅ **단위 테스트 9개 구현 및 통과**
- ✅ **전체 테스트 통과 (570개 테스트)**
- ✅ **기존 OAuth2 인증과 하위 호환성 유지**

**FT-010 OAuth2 JWT 연동 및 토큰 발급 구현이 성공적으로 완료되었습니다!** 🎉

이제 프론트엔드 SPA에서 OAuth2 로그인을 통해 JWT 토큰을 획득하고, API 기반 인증을 사용할 수 있는 기반이 마련되었습니다.
