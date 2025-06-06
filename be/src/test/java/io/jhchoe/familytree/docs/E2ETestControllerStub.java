package io.jhchoe.familytree.docs;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class E2ETestControllerStub {

    @GetMapping("/api/test/e2e-rest-docs")
    public ResponseEntity<E2ETestResponseBody> e2eTest(@AuthFTUser FTUser user) {
        E2ETestResponseBody body = new E2ETestResponseBody("이름", 20, "부서");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/api/test/e2e-rest-docs-2")
    public ResponseEntity<E2ETestResponseBody> e2eTest2(@AuthFTUser FTUser user) {
        E2ETestResponseBody body = new E2ETestResponseBody("이름", 20, "부서");
        return ResponseEntity.ok(body);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class E2ETestResponseBody {
        private String name;
        private int age;
        private String dept;
    }
}
