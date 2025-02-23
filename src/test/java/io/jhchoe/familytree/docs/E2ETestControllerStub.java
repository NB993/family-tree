package io.jhchoe.familytree.docs;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.support.ApiResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class E2ETestControllerStub {

    @GetMapping("/api/test/e2e-rest-docs")
    public ApiResponse<E2ETestResponseBody> e2eTest(@AuthFTUser FTUser user) {
        E2ETestResponseBody body = new E2ETestResponseBody();
        body.setName("testName");
        body.setAge(10);
        body.setDept("testDept");
        return ApiResponse.success(body);
    }

    @Getter
    @Setter
    static class E2ETestResponseBody {
        private String name;
        private int age;
        private String dept;
    }
}
