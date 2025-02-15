package io.jhchoe.familytree.common.auth;

import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@RequiredArgsConstructor
@Controller
@Slf4j
public class SignupController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
        @RequestParam("username") String username,
        @RequestParam("password") String password
    ) {
        try {
            userService.createUser(username, password);
            return "redirect:/login";
        } catch (Exception e) {
            log.error("회원가입 중 에러", e.getCause());
            return "signup";
        }
    }

    @GetMapping("/success")
    @ResponseBody
    public String longinSuccess(@AuthFTUser FTUser ftUser) {
        log.debug("ftUser: {}", ftUser);
        return "hi login user";
    }
}
