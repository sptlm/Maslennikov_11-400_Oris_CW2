package kfu.itis.maslennikov.controller;

import kfu.itis.maslennikov.dto.RegisterDto;
import kfu.itis.maslennikov.service.impl.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registerDto") RegisterDto registerDto, Model model) {
        try {
            userService.register(registerDto);
            return "success_sign_up";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/verification")
    public String verify(@RequestParam("code") String code, Model model) {
        boolean verified = userService.verify(code);
        model.addAttribute("verified", verified);
        return "verification_result";
    }
}
