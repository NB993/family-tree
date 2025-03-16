package io.jhchoe.familytree.docs;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
abstract class ApiDocsTestBase {

    @Autowired
    private WebApplicationContext applicationContext;

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
