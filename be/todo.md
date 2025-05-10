# 테스트 환경 구성 개선 TODO

## 목표
통합 테스트에서 테스트 컨테이너(Testcontainers)를 사용하여 테스트 간 격리 문제를 해결하고 실제 환경과 유사한 테스트 환경을 구축합니다.

## 현재 문제점
- 통합 테스트 실행 시 Spring 컨텍스트 로드 오류 발생
- `FamilyAdapter`가 `CreateFamilyPort`와 `LoadFamilyPort` 인터페이스를 모두 구현해야 하는데, 현재는 `LoadFamilyPort` 타입으로만 모킹되어 있어 문제 발생
- 테스트 간 격리가 잘 이루어지지 않음

## 개선 계획
### 1. 의존성 추가
```gradle
testImplementation 'org.testcontainers:testcontainers:1.19.2'
testImplementation 'org.testcontainers:junit-jupiter:1.19.2'
testImplementation 'org.testcontainers:mysql:1.19.2'  // 또는 사용하는 DB에 맞게 변경
```

### 2. 테스트 클래스 설정
```java
@SpringBootTest
@Testcontainers
class FamilyRelationshipControllerTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private WebApplicationContext context;
    
    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);
    }
    
    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.webAppContextSetup(context);
        
        // 테스트 데이터 설정
        // ...
    }
    
    // 테스트 메서드
}
```

### 3. 테스트 컨테이너 재사용 설정 (선택사항)
```java
@Container
@ServiceConnection
private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true);  // 재사용 설정
```

`~/.testcontainers.properties` 파일에 다음 설정 추가:
```
testcontainers.reuse.enable=true
```

### 4. FamilyAdapter 수정
`FamilyAdapter` 클래스가 필요한 모든 인터페이스를 구현하도록 수정:
```java
@Component
public class FamilyAdapter implements CreateFamilyPort, LoadFamilyPort {
    // 두 인터페이스의 메서드 모두 구현
}
```

## 고려사항
- 각 테스트 메서드 전에 데이터를 적절히 초기화하는 방법 구현
- 테스트 성능 최적화를 위한 컨테이너 재사용 설정
- 필요한 경우 트랜잭션 관리 추가 (@Transactional 등)
- 테스트 실행 시간 모니터링 및 최적화

## 이점
- 실제 환경과 유사한 테스트 수행 가능
- 테스트 간 완벽한 격리 보장
- 특정 데이터베이스의 고유한 기능까지 테스트 가능
- 다중 서비스 환경(DB, 메시징 시스템 등) 테스트 가능
