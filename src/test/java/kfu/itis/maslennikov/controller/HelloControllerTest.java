package kfu.itis.maslennikov.controller;

import kfu.itis.maslennikov.service.impl.HelloService;
import kfu.itis.maslennikov.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HelloService helloService;

    @MockitoBean
    private UserService userService;

    @Test
    void helloShouldReturnGreetingForProvidedName() throws Exception {
        given(helloService.sayHello("Ivan")).willReturn("Hello, Ivan");

        mockMvc.perform(get("/hello")
                        .param("name", "Ivan")
                        .with(user("ivan").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Ivan"));
    }

    @Test
    void helloShouldReturnGreetingWithoutName() throws Exception {
        given(helloService.sayHello(null)).willReturn("Hello, World");

        mockMvc.perform(get("/hello")
                        .with(user("ivan").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World"));
    }
}