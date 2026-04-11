package kfu.itis.maslennikov.controller;

import kfu.itis.maslennikov.dto.RegisterDto;
import kfu.itis.maslennikov.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(RegisterController.class)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void showFormShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register").with(user("ivan").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerDto"));
    }

    @Test
    void registerShouldReturnSuccessViewWhenRegistrationSucceeds() throws Exception {
        mockMvc.perform(post("/register")
                        .with(user("ivan").roles("USER"))
                        .with(csrf())
                        .param("username", "ivan")
                        .param("email", "ivan@mail.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("success_sign_up"));
    }

    @Test
    void registerShouldReturnFormWithErrorWhenValidationFails() throws Exception {
        doThrow(new IllegalArgumentException("Имя пользователя уже занято"))
                .when(userService).register(any(RegisterDto.class));

        mockMvc.perform(post("/register")
                        .with(user("ivan").roles("USER"))
                        .with(csrf())
                        .param("username", "ivan")
                        .param("email", "ivan@mail.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("error", "Имя пользователя уже занято"));
    }

    @Test
    void verifyShouldReturnVerificationResultView() throws Exception {
        given(userService.verify("ok-code")).willReturn(true);

        mockMvc.perform(get("/verification").param("code", "ok-code").with(user("ivan").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("verification_result"))
                .andExpect(model().attribute("verified", true));
    }
}
