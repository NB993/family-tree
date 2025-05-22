# QA 공통 가이드라인

## 계층별 단계적 QA 전략

### QA 실행 단계
우리는 **계층별 단계적 QA**를 통해 각 개발 단계마다 해당 계층의 품질을 검증합니다.

```markdown
1단계: 코어 계층(application) QA
   - 도메인 모델 및 비즈니스 로직 검증
   - UseCase 시나리오 검증
   - 권한 검증 로직 테스트
   - 단위 테스트 및 서비스 통합 테스트

2단계: 인프라 계층(adpater/out) QA
   - 데이터 영속성 검증
   - Repository 계층 테스트
   - 트랜잭션 경계 테스트

3단계: 프레젠테이션 계층(adpater/in) QA
   - API 엔드포인트 검증
   - HTTP 상태 코드 테스트
   - E2E 시나리오 테스트

4단계: 통합 QA (Integration QA) - 추후 진행 예정
   - 전체 시스템 검증
   - 성능 테스트
   - 보안 테스트
   - 현재 개발 단계에서는 수행하지 않으며, 향후 시스템 완성도가 높아진 후 진행할 예정
```

### 계층별 QA 범위

#### 1단계: 코어 계층(application) QA
**목적**: 핵심 비즈니스 로직 및 애플리케이션 서비스 검증
- ✅ 도메인 모델 생성 및 변경 로직
- ✅ 비즈니스 룰 준수 여부
- ✅ 도메인 이벤트 발생
- ✅ 예외 상황 처리
- ✅ UseCase 구현체 로직
- ✅ Command/Query 처리
- ✅ 권한 검증 로직
- ✅ 트랜잭션 경계 설정
- ✅ 외부 의존성 Mocking
- ❌ 데이터베이스 연동 (Mocking 사용)
- ❌ HTTP 요청/응답

#### 2단계: 인프라 계층(adapter/out) QA
**목적**: 데이터 영속성과 외부 시스템 연동 검증
- ✅ Repository 구현체 동작
- ✅ JPA Entity 매핑
- ✅ 데이터베이스 제약 조건
- ✅ 트랜잭션 롤백/커밋
- ✅ 실제 데이터베이스 연동
- ❌ HTTP API 레벨 테스트

#### 3단계: 프레젠테이션 계층(adapter/in) QA
**목적**: API 인터페이스와 사용자 인터랙션 검증
- ✅ REST API 엔드포인트
- ✅ HTTP 상태 코드
- ✅ Request/Response DTO
- ✅ 인증/인가 통합
- ✅ 에러 응답 형식
- ✅ API 문서 일치성

#### 4단계: 통합 QA (추후 진행 예정)
**목적**: 전체 시스템의 종합적 품질 검증
- 📋 E2E 사용자 시나리오
- 📋 성능 테스트
- 📋 보안 테스트
- 📋 동시성 테스트
- 📋 장애 복구 테스트

> **참고**: 4단계 통합 QA는 현재 개발 단계에서는 수행하지 않으며, 시스템의 기본 기능들이 모두 구현되고 안정화된 후 추가로 진행할 예정입니다.

## 테스트 자동화 전략

### 계층별 테스트 도구 및 접근법

#### 1단계: 코어 계층 테스트
- **도구**: JUnit 5, AssertJ, Mockito, Spring Boot Test
- **접근법**: 
  - **도메인 테스트**: Pure Unit Test (외부 의존성 없음)
    - 도메인 모델 상태 변화 검증
    - 비즈니스 규칙 위반 시 예외 검증
  - **서비스 테스트**: Service 단위 테스트 (포트 Mocking)
    - UseCase 시나리오 테스트
    - 권한 검증 통합 테스트
- **커버리지 목표**: 90% 이상
- **실행 주기**: 코드 변경 시마다 실행

```java
// 예시: 도메인 테스트
@Test
@DisplayName("OWNER 역할 변경 불가 검증")
void owner_역할은_변경할_수_없어야_함() {
    // given
    FamilyMember owner = FamilyMember.newOwner(1L, 1L, "name", null, null, "KR");
    
    // when & then
    assertThatThrownBy(() -> owner.updateRole(FamilyMemberRole.ADMIN))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Cannot change role of the Family OWNER");
}

// 예시: 서비스 테스트
@ExtendWith(MockitoExtension.class)
class ModifyFamilyMemberRoleServiceTest {
    @Mock FindFamilyMemberPort findFamilyMemberPort;
    @Mock ModifyFamilyMemberPort modifyFamilyMemberPort;

    @Test
    @DisplayName("OWNER만 권한 변경 가능")
    void only_owner_can_modify_role() {
        // Service 로직 검증
    }
}
```

#### 2단계: 인프라 계층 테스트
- **도구**: Spring Boot Test, TestContainers, @DataJpaTest
- **접근법**:
  - Repository 슬라이스 테스트
  - 실제 데이터베이스 연동 테스트
  - JPA 쿼리 검증
- **커버리지 목표**: 85% 이상
- **실행 주기**: 배포 전 필수 실행

```java
// 예시: 인프라 계층 테스트  
@DataJpaTest
@Testcontainers
class FamilyMemberRepositoryTest {
    @Test
    @DisplayName("역할별 구성원 조회 쿼리 검증")
    void find_members_by_role_query_test() {
        // Repository 쿼리 검증
    }
}
```

#### 3단계: 프레젠테이션 계층 테스트
- **도구**: REST Assured, SpringBootTest
- **접근법**:
  - DB 데이터 연동
  - API 계약 테스트
  - 인증/인가 통합 테스트
- **커버리지 목표**: 80% 이상
- **실행 주기**: 배포 전 필수 실행

```java
// 예시: 프레젠테이션 계층 테스트
@DisplayName("[Acceptance Test] ModifyFamilyMemberRoleControllerTest")
class ModifyFamilyMemberRoleControllerTest extends AcceptanceTestBase {
    @Test
    @DisplayName("권한 변경 API 정상 응답 검증")
    void modify_role_api_success_response() {
        // API 응답 검증
    }
}
```

## 버그 리포팅 및 추적 체계

### 1. 버그 심각도 분류

#### Critical (심각)
- **정의**: 시스템 다운, 데이터 손실, 보안 취약점, 핵심 기능 완전 장애
- **대응 시간**: 즉시 (1시간 이내)

#### High (높음)
- **정의**: 주요 기능 부분 장애, 성능 심각한 저하, 다수 사용자 영향
- **대응 시간**: 당일 내 (8시간 이내)

#### Medium (보통)
- **정의**: 일부 기능 오동작, UI/UX 문제, 특정 조건에서만 발생
- **대응 시간**: 3일 이내

#### Low (낮음)
- **정의**: 사소한 표시 오류, 개선 사항, 문서 오타
- **대응 시간**: 1주일 이내

### 2. 버그 리포트 템플릿

```markdown
## 버그 정보
- **ID**: BUG-YYYY-NNNN
- **제목**: [간단한 버그 설명]
- **발견 계층**: Core/Infrastructure/Presentation
- **심각도**: Critical/High/Medium/Low
- **우선순위**: P0/P1/P2/P3
- **발견자**: [이름]
- **발견일**: YYYY-MM-DD
- **담당자**: [할당된 개발자]

## 환경 정보
- **테스트 단계**: 1단계(코어)/2단계(인프라)/3단계(프레젠테이션)
- **OS**: 
- **버전**: 
- **테스트 환경**: Local/Dev/Staging/Production

## 재현 방법
1. [단계별 재현 방법]
2. 
3. 

## 예상 결과
[정상적인 동작 설명]

## 실제 결과
[실제 발생한 문제 설명]

## 계층별 영향도 분석
- **코어 계층**: [영향 여부 및 설명]
- **인프라 계층**: [영향 여부 및 설명]
- **프레젠테이션 계층**: [영향 여부 및 설명]

## 추가 정보
- **스크린샷**: [첨부]
- **로그**: [관련 로그]
- **에러 메시지**: [정확한 에러 내용]
- **영향 범위**: [영향받는 사용자/기능 범위]
- **관련 테스트**: [실패한 테스트 케이스]

## 해결 방안 (개발자 작성)
- **원인 분석**: 
- **해결 방법**: 
- **테스트 방법**: 
- **계층별 수정 사항**:
  - Core: 
  - Infrastructure:
  - Presentation:
- **배포 계획**: 
```

### 3. 계층별 버그 분류 기준

#### Core Layer 버그
- 도메인 모델 상태 변화 오류
- 비즈니스 규칙 위반
- UseCase 시나리오 오류
- 권한 검증 로직 오류
- Command/Query 처리 오류

#### Infrastructure Layer 버그
- 데이터 영속성 오류
- JPA 매핑 문제
- 쿼리 성능 이슈
- 트랜잭션 롤백 실패

#### Presentation Layer 버그
- API 응답 형식 오류
- HTTP 상태 코드 오류
- 인증/인가 오류
- Request/Response 매핑 오류

## 회귀 테스트 전략

### 계층별 회귀 테스트 범위

#### 1단계: 코어 계층 회귀 테스트
**목적**: 비즈니스 로직 및 애플리케이션 서비스 변경이 기존 기능에 미치는 영향 검증
- **범위**: 모든 도메인 모델 + UseCase 단위 테스트
- **실행 주기**: 매 코드 변경 시
- **자동화 수준**: 100%

**핵심 검증 항목**:
- [ ] 도메인 모델 생성 및 비즈니스 로직
- [ ] UseCase 시나리오 정상 동작
- [ ] 권한 검증 로직
- [ ] Command/Query 처리

#### 2단계: 인프라 계층 회귀 테스트
**목적**: 데이터 계층 변경이 기존 영속성에 미치는 영향 검증  
- **범위**: Repository 테스트 + 상위 계층 통합 테스트
- **실행 주기**: 일일 빌드 시
- **자동화 수준**: 90%

**핵심 검증 항목**:
- [ ] 데이터 영속성 보장
- [ ] 쿼리 정확성
- [ ] 트랜잭션 경계
- [ ] 코어 계층 연동

#### 3단계: 프레젠테이션 계층 회귀 테스트
**목적**: API 변경이 기존 클라이언트에 미치는 영향 검증
- **범위**: API 계약 테스트 + 전체 스택 통합 테스트  
- **실행 주기**: 배포 전
- **자동화 수준**: 85%

**핵심 검증 항목**:
- [ ] API 응답 형식 일관성
- [ ] HTTP 상태 코드
- [ ] 인증/인가 정상 동작
- [ ] 하위 계층 연동

### 실행 전략

#### 계층별 실행 순서
```markdown
1. 변경된 계층의 단위 테스트 먼저 실행
2. 해당 계층의 회귀 테스트 실행  
3. 상위 계층들의 관련 테스트 실행
```

#### 실패 시 대응 전략
```markdown
1단계 실패 → 코어 로직 수정 후 재테스트
2단계 실패 → 인프라 설정 검토 + 1단계 재실행
3단계 실패 → API 계약 검토 + 1,2단계 재실행
```

## 품질 메트릭

### 1. 품질 메트릭
- **버그 발견율**: 단위 시간당 발견되는 버그 수
- **버그 해결율**: 단위 시간당 해결되는 버그 수
- **버그 재발율**: 수정 후 재발하는 버그 비율
- **평균 해결 시간**: 버그 발견부터 해결까지 소요 시간
- **테스트 커버리지**: 코드 커버리지 비율

### 2. 성능 메트릭 (모니터링 목적)
- **응답 시간**: API별 평균/최대 응답 시간 추이 모니터링
- **처리량**: 단위 시간당 처리 가능한 요청 수 측정
- **에러율**: 전체 요청 대비 에러 발생 비율 추적
- **리소스 사용률**: CPU, 메모리, 디스크 사용률 모니터링

> **참고**: 현재 단계에서는 성능 기준을 설정하지 않고 베이스라인 데이터 수집 및 모니터링에 집중합니다.

## 품질 게이트

### 1. 코드 품질 기준
- **테스트 커버리지**: 90% 이상
- **정적 분석**: SonarQube 품질 게이트 통과
- **코드 리뷰**: 최소 2명 이상 승인

### 2. 성능 기준 (모니터링)
- **성능 모니터링**: 베이스라인 대비 성능 저하 추이 관찰
- **부하 테스트**: 현재 단계에서는 수행하지 않음 (추후 진행 예정)
- **메모리 누수**: 장시간 실행 시 메모리 증가 패턴 모니터링

### 3. 보안 기준
- **보안 스캔**: 취약점 0개
- **권한 검증**: 모든 API 엔드포인트 권한 검증 완료
- **데이터 검증**: 입력값 검증 로직 구현
