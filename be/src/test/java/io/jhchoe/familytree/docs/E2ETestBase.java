package io.jhchoe.familytree.docs;


import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.jhchoe.familytree.common.config.SecurityConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class E2ETestBase {

    @LocalServerPort
    protected int port;

    protected RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
            .setPort(port)
            .addFilter(documentationConfiguration(restDocumentation))
            .build();
    }
}
