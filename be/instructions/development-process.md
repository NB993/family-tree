# 개발 프로세스 및 단계

## 계층별 단계적 개발 전략

### 개발 단계 및 QA 주기
백엔드 개발은 **계층별 단계적 접근**을 통해 진행하며, 각 단계 완료 후 해당 계층의 QA를 수행합니다.

```markdown
1단계: 코어 계층 개발 → 코어 계층 QA
   - 도메인 모델, 비즈니스 로직 구현
   - UseCase, Service, Command/Query 구현
   - 포트 인터페이스 정의
   - 단위 테스트 작성 및 검증

2단계: 인프라 계층 개발 → 인프라 계층 QA
   - Repository, JPA Entity, Adapter 구현
   - 데이터 영속성 테스트 작성 및 검증

3단계: 프레젠테이션 계층 개발 → 프레젠테이션 계층 QA
   - Controller, Request/Response DTO 구현
   - API 테스트 작성 및 검증

4단계: 통합 QA
   - E2E 테스트, 성능 테스트, 보안 테스트
```

### 계층별 개발 완료 기준

#### 1단계: 코어 계층 완료 기준
- ✅ 모든 도메인 모델 구현 완료
- ✅ 비즈니스 로직 메서드 구현 완료
- ✅ 모든 UseCase 인터페이스 및 구현체 완료
- ✅ Command/Query 객체 구현 완료
- ✅ 포트 인터페이스 정의 완료
- ✅ 도메인 및 서비스 단위 테스트 90% 이상 커버리지
- ✅ 코어 계층 QA 통과

#### 2단계: 인프라 계층 완료 기준
- ✅ 모든 Repository 구현체 완료
- ✅ JPA Entity 매핑 완료
- ✅ 아웃바운드 어댑터 구현 완료
- ✅ 데이터베이스 연동 테스트 85% 이상 커버리지
- ✅ 인프라 계층 QA 통과

#### 3단계: 프레젠테이션 계층 완료 기준
- ✅ 모든 Controller 구현 완료
- ✅ Request/Response DTO 구현 완료
- ✅ 인바운드 어댑터 구현 완료
- ✅ API 테스트 80% 이상 커버리지
- ✅ 프레젠테이션 계층 QA 통과

#### 4단계: 통합 완료 기준
- ✅ E2E 테스트 주요 시나리오 100% 커버
- ✅ 성능 측정 및 모니터링 설정
- ✅ 보안 테스트 통과
- ✅ 통합 QA 통과

## 기능 개발 템플릿

아키텍트/개발자 AI는 새로운 기능 개발을 시작할 때 `4_feat/{기능명}.md` 파일에 아래 템플릿을 작성하여 활용합니다:

```markdown
# 기능 개발 지침

## 개발할 기능
- 개발할 기능 설명

## 기획 및 유효성 검증 정책
- [ ] 항목 1
- [ ] 항목 2
- [ ] ...

## 개발 및 QA 단계
- [ ] 1단계: 코어 계층(application) 개발
- [ ] 1단계: 코어 계층(application) QA 검증
- [ ] 2단계: 인프라 계층(adapter/out) 개발
- [ ] 2단계: 인프라 계층(adapter/out) QA 검증
- [ ] 3단계: 프레젠테이션 계층(adapter/in) 개발
- [ ] 3단계: 프레젠테이션 계층(adapter/in) QA 검증
```

## 단계별 개발 프로세스

개발은 다음과 같은 단계별 순서로 진행합니다:

### 1. 코어 계층 개발 (application 계층)
- inbound port(유스 케이스) 추가 또는 기존 inbound port에 메서드 추가
- 유스 케이스에 전달할 Command 또는 Query 클래스 추가
- 유스 케이스 구현체 Service 클래스 추가 또는 기존 Service 클래스에 추가된 유스 케이스 메서드 구현
- 유스 케이스 구현에 필요한 도메인 엔티티 추가 또는 기존 도메인 엔티티에 메서드 추가
- Service 클래스에 주입할 outbound port 추가 또는 기존 outbound port에 메서드 추가
- Command 또는 Query가 추가되었다면 단위 테스트 작성
  - 테스트 클래스에 `@DisplayName("[Unit Test] {Command 또는 Query 클래스명}Test"` 선언
- 도메인 엔티티 단위 테스트 작성
  - 테스트 클래스에 `@DisplayName("[Unit Test] {도메인 엔티티 명}Test"` 선언
- Service 클래스 단위 테스트 작성
  - 테스트 클래스에 `@DisplayName("[Unit Test] {Service 클래스명}Test"` 선언
  - Service 변수에 `@InjectMocks` 선언
  - Service 클래스가 의존하는 outbound port는 `@MockitoBean`을 선언하여 Mocking
    - 각 Mocking 코드에 주석으로 Mocking의 의도를 설명

### 2. 인프라 계층 개발 (adapter/out 계층)
- outbound port 구현체 Adapter 추가 또는 기존 Adapter 클래스에 추가된 outbound port 메서드 구현
- outbound port 구현에 필요한 도메인JpaEntity 추가 또는 기존 도메인JpaEntity에 메서드 추가
- Adapter 클래스 단위 테스트 작성
  - 테스트 클래스
    - [AdapterTestBase.java](src/test/java/io/jhchoe/familytree/helper/AdapterTestBase.java) 상속
    - `@DisplayName("[Unit Test] {Adapter 클래스명}Test"` 선언
    - 필요한 JpaRepository @Autowired
    - 테스트 대상 Adapter를 변수명 `sut` private 으로 선언
    - `@BeforeEach setUp()` 메서드에서 sut에 필요한 JpaRepository을 생성자로 주입하여 sut 초기화
- 도메인JpaEntity 클래스 단위 테스트 작성
  - `@DisplayName("[Unit Test] {도메인JpaEntity 클래스명}Test"` 선언

### 3. 프레젠테이션 계층 개발 (adapter/in 계층)
- 필요시 요청 DTO, 응답 DTO 추가
- inbound Adapter(Controller 클래스) 및 API 엔드포인트 추가
- Controller는 유스 케이스를 주입한다 (not Service)
- 인수 테스트 작성
  - 테스트 클래스
    - [AcceptanceTestBase.java](src/test/java/io/jhchoe/familytree/docs/AcceptanceTestBase.java) 상속
  - `@DisplayName("[Acceptance Test] {Controller 클래스명}Test")` 선언
  - 무조건 DB 데이터로 테스트. 기본적으로 mocking 미사용 정책.
  - DB 데이터 생성을 위한 JpaRepository를 @Autowired
  - DB 데이터 생성을 위해 도메인JpaEntity 생성 시 기본생성자 절대 사용 금지. 무조건 도메인 엔티티의 신규 엔티티 생성용 정적 메서드를 이용하여 도메인 엔티티를 생성한 뒤 도메인JpaEntity.from 메서드를 호출하여 생성
  - 절대 `@BeforeEach`에서 테스트용 데이터 생성 하지 말 것. 각 테스트 메서드의 given 영역에서 데이터 생성
  - GET 메서드 이외에는 `.given()` 다음에 `.postProcessors(SecurityMockMvcRequestPostProcessors.csrf())` 무조건 설정
  - 요청 Body 데이터는 Multiline Strings(`"""`)를 이용
  - 응답 Body 검증 시 RestAssured 내장 함수 활용
  - API 내에서 발생 가능한 모든 예외 케이스를 테스트 (inbound ~ application ~ oubound 전체 계층)

## 개발 규칙 준수사항

- 개발 전 반드시 아키텍처 및 명명 규칙 문서를 읽고 규칙에 맞춰 개발해야 합니다
- 목록에서 체크표시 되지 않은 항목부터 순서대로 개발해야 합니다
- 각 계층(코어, 인프라, 프레젠테이션)을 개발하고 테스트 코드 작성을 완료한 후에는 해당 항목을 체크 처리하고 작업을 종료합니다
- 다음 계층을 개발하기 전에 반드시 사람의 추가 지시를 대기해야 합니다
- 개발할 기능의 시작 부분에 개발할 기능과 유효성 검증 정책을 명확히 정의해야 합니다

## 개발 전 필수 확인사항 ✓

1. [ ] 역할별 AI들이 작성한 산출물들을 읽었습니까?
2. [ ] 도메인별 행동 명명 규칙(Find/Save/Modify/Delete)을 숙지했습니까?
3. [ ] 계층별 클래스 명명 패턴을 이해했습니까?
4. [ ] 도메인 모델의 불변성과 캡슐화 원칙을 이해했습니까?
5. [ ] 헥사고날 아키텍처의 포트와 어댑터 분리 개념을 이해했습니까?
6. [ ] 예외 처리 방식과 FTException 활용법을 숙지했습니까?
7. [ ] 주석과 JavaDoc 작성 어투/패턴을 확인했습니까?
8. [ ] 테스트 코드 작성 방법과 네이밍 규칙을 이해했습니까?

## 코드 제출 전 최종 점검사항 ✓

1. [ ] **명명 규칙 준수**: 모든 접두사가 Find/Save/Modify/Delete 중 올바른 것으로 사용되었는지 확인
   - 조회 기능: Find만 사용 (Get/Retrieve/Query 사용 금지)
   - 등록 기능: Save만 사용 (Create/Add/Insert 사용 금지)
   - 수정 기능: Modify만 사용 (Update/Change/Edit 사용 금지)
   - 삭제 기능: Delete만 사용 (Remove/Erase 사용 금지)
2. [ ] **계층별 클래스명 확인**: 각 계층(UseCase, Port, Controller 등)의 클래스명이 지정된 패턴을 따르는지 확인
3. [ ] **호환성 검토**: 메서드명이나 시그니처를 변경한 경우, 해당 메서드를 호출하는 모든 코드가 정상 작동하는지 확인
4. [ ] **도메인 로직 캡슐화**: 비즈니스 로직이 도메인 객체 내부에 적절히 캡슐화되었는지 확인
5. [ ] **포트/어댑터 분리**: 포트와 어댑터 계층이 명확하게 분리되어 있는지 확인
6. [ ] **예외 발생 위치**: 예외가 유스케이스에서만 발생하고 어댑터에서 발생하지 않는지 확인
7. [ ] **주석 어투 일관성**: 모든 주석과 JavaDoc이 지정된 어투와 패턴을 따르는지 확인
8. [ ] **테스트 코드 작성**: 모든 유스케이스에 대한 단위 테스트가 작성되었는지 확인
9. [ ] **코드 스타일 일관성**: 코드스타일과 네이밍이 프로젝트 전체에서 일관되게 적용되었는지 확인
