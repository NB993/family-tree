# 기능 개발 지침

## 개발할 기능 (이곳을 변경)
- Family 가입 신청 기능

## 기획 및 유효성 검증 정책 (이곳을 변경)
- [ ] 이미 가입한 Family에는 가입 신청 불가
- [ ] Family 가입 가능 수는 유저당 최대 5개
- [ ] 가입 거부 처리되어 있는 사용자는 신청 불가
  - 예외 문구는 '가입 신청이 불가능합니다.'
- [ ] Family 가입 신청 객체는 '대기', '승인', '거절' 상태를 가진다
  - 특정 Family에 가입 신청한 적이 있고, 가장 최근에 저장된 Family 가입 신청 엔티티의 상태가 '대기' 또는 '승인'인 경우 가입 신청 불가
    - 예외 문구는 Family 가입 신청 상태가 '대기'인 경우 '가입 신청을 처리중이에요.', '승인'인 경우 '가입된 패밀리입니다.'


## 개발 단계
- [x] application 계층 개발
- [ ] adapter/out 계층 개발
- [ ] adapter/in 계층 개발

## 개발 규칙
- 개발 전 반드시 아래 파일을 읽고 규칙에 맞춰 개발해야 한다.
  - [be-instruction.md](be-instruction.md)
- [개발 단계](#개발-단계) 목록에서 체크표시 되지 않은 항목부터 순서대로 개발
  - 목록 하나를 처리하면 checked 처리 후 작업 종료. 추가 지시 대기.
- [개발 단계](#개발-단계) 단계별 개발 규칙
  - application 계층 개발
    - inbound port(유스 케이스) 추가, 또는 기존 inbound port(유스 케이스)에 메서드 추가
    - 유스 케이스에 전달할 Command 또는 Query 클래스 추가
    - 유스 케이스 구현체 Service 클래스 추가, 또는 기존 Service 클래스에 추가된 유스 케이스 메서드 구현
    - 유스 케이스 구현에 필요한 도메인 엔티티 추가, 또는 기존 도메인 엔티티에 메서드 추가
    - Service 클래스에 주입할 outbound port 추가, 또는 기존 outbound port에 메서드 추가
    - Command 또는 Query가 추가되었다면 단위 테스트 작성
      - 테스트 클래스에 `@DisplayName("[Unit Test] {Command 또는 Query 클래스명}Test` 선언
    - 도메인 엔티티 단위 테스트 작성
      - 테스트 클래스에 `@DisplayName("[Unit Test] {도메인 엔티티 명}Test` 선언
    - Service 클래스 단위 테스트 작성
      - 테스트 클래스에 `@DisplayName("[Unit Test] {Service 클래스명}Test` 선언
      - Service 변수에 `@InjectMocks` 선언
      - Service 클래스가 의존하는 outbound port는 `@MockitoBean`를 선언하여 Mocking
        - 각 Mocking 코드에 주석으로 Mocking의 의도를 설명
  - adapter/out 계층 개발
    - 추가된 outbound port 구현체 Adapter 추가, 또는 기존 Adapter 클래스에 추가된 outbound port 메서드 구현
    - outbound port 구현에 필요한 도메인JpaEntity 추가, 또는 기존 도메인JpaEntity에 메서드 추가
    - Adapter 클래스 단위 테스트 작성
      - 테스트 클래스
        - [AdapterTestBase.java](src/test/java/io/jhchoe/familytree/helper/AdapterTestBase.java) 상속
        - `@DisplayName("[Unit Test] {Adapter 클래스명}Test` 선언
        - 필요한 JpaRepository @Autowired
        - 테스트 대상 Adapter를 변수명 `sut` private 으로 선언
        - `@BeforeEach setUp()` 메서드에서 sut에 필요한 JpaRepository을 생성자로 주입하여 sut 초기화
    - 도메인JpaEntity 클래스 단위 테스트 작성
      - `@DisplayName("[Unit Test] {도메인JpaEntity 클래스명}Test` 선언
  - adapter/in 계층 개발
    - 필요시 요청 DTO, 응답 DTO 추가
    - inbound Adapter(Controller 클래스) 및 API 엔드포인트 추가
      - 하나의 Controller에는 반드시 하나의 API만 작성한다
    - Controller는 유스 케이스를 주입한다 (not Service)
    - 인수 테스트 작성
      - 테스트 클래스
        - [AcceptanceTestBase.java](src/test/java/io/jhchoe/familytree/docs/AcceptanceTestBase.java) 상속
      - `@DisplayName("[Acceptance Test] {Controller 클래스명}Test")` 선언
      - 무조건 DB 데이터로 테스트. 기본적으로 mocking 미사용 정책.
      - DB 데이터 생성을 위한 JpaRepository를 @Autowired
      - 절대 `@BeforeEach`에서 테스트용 데이터 생성 하지 말 것. 각 테스트 메서드의 given 영역에서 데이터 생성
      - 응답 Body 검증 시 RestAssured 내장 함수 활용
      - API 내에서 발생 가능한 모든 예외 케이스를 테스트 (inbound ~ application ~ oubound 전체 계층)
