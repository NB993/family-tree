package io.jhchoe.familytree.docs;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;

import io.jhchoe.familytree.helper.TestcontainersModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Spring RestDocs + Testcontainers MySQL 조합을 위한 베이스 클래스
 * 
 * 특징:
 * - @SpringBootTest로 전체 애플리케이션 컨텍스트 로드
 * - MySQL Testcontainer로 프로덕션 환경과 동일한 데이터베이스
 * - RestDocs를 통한 API 문서 자동 생성
 * - MockMvc + RestAssured 조합으로 테스트 작성 편의성 제공
 * - Spring Security 통합 테스트 지원
 * - 트랜잭션 롤백으로 테스트 격리
 * 
 * 사용 예시:
 * @DisplayName("[API Docs] Family API")
 * class FamilyApiDocsTest extends ApiDocsTestBase {
 *     @Test
 *     void 가족_생성_API_문서화() {
 *         given(this.spec)
 *             .filter(document("family-create"))
 *             .when()
 *             .post("/api/families")
 *             .then()
 *             .status(HttpStatus.CREATED);
 *     }
 * }
 */
@ActiveProfiles("testcontainers")  // H2 대신 MySQL Testcontainer 사용
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
@Transactional  // 각 테스트 후 롤백으로 데이터 격리
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // 컨테이너 재사용으로 성능 최적화
public abstract class ApiDocsTestBase {

    @Autowired
    private WebApplicationContext applicationContext;

    // MySQL Testcontainer 시작 (클래스 로딩 시 최초 1회)
    static {
        TestcontainersModule.startMySQLContainer();
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        //RestAssuredMockMvc는 MockMvc 세팅을 이용해서 구성하기 때문에 'org.springframework.restdocs:spring-restdocs-mockmvc' 의존성이 필요하다.
        //RestDocs 설정을 추가할 때 spring-restdocs-mockmvc의 MockMvcDocumentaion 클래스를 이용해야 한다.
        MockMvc build = MockMvcBuilders.webAppContextSetup(applicationContext)
            .apply(org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

        mockMvc(build);
    }
}
