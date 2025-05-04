package io.jhchoe.familytree.common.auth.controller;

import io.jhchoe.familytree.common.auth.service.UserService;
import io.jhchoe.familytree.common.auth.domain.AuthFTUser;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/login")
    public String loginPage(
        @RequestParam(value = "error", required = false) String error,
        Model model
    ) {
        if ("true".equals(error)) {
            model.addAttribute("error", "로그인에 실패했습니다. 아이디와 비밀번호를 확인하세요.");
        }
        return "login"; // login.html 반환
    }

    @GetMapping
    @ResponseBody
    public String index() {
        return "로그인성공";
    }
}
