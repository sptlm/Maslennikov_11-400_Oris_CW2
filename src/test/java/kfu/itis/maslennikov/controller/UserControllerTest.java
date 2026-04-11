package kfu.itis.maslennikov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kfu.itis.maslennikov.dto.UserDto;
import kfu.itis.maslennikov.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllUsers() throws Exception {
        UserDto userDto = new UserDto(1L, "Ivan","coc");
        given(userService.findAll()).willReturn(Arrays.asList(userDto));
        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("Ivan"));
    }

    @Test
    void getByIdCreateUpdateDeleteAndNotFoundFlows() throws Exception {
        UserDto userDto = new UserDto(1L, "Ivan", "ivan@mail.com");

        given(userService.findById(1L)).willReturn(userDto);
        mockMvc.perform(get("/users/1").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        given(userService.findById(404L)).willThrow(new NoSuchElementException("User not found"));
        mockMvc.perform(get("/users/404").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());

        given(userService.create(org.mockito.ArgumentMatchers.any(UserDto.class))).willReturn(userDto);
        mockMvc.perform(post("/users")
                        .with(user("admin").roles("ADMIN")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Ivan"));

        given(userService.update(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(UserDto.class)))
                .willReturn(userDto);
        mockMvc.perform(put("/users/1")
                        .with(user("admin").roles("ADMIN")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        given(userService.update(org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any(UserDto.class)))
                .willThrow(new NoSuchElementException("User not found"));
        mockMvc.perform(put("/users/2")
                        .with(user("admin").roles("ADMIN")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/users/1").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isNoContent());

        doThrow(new NoSuchElementException("User not found")).when(userService).delete(404L);
        mockMvc.perform(delete("/users/404").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
