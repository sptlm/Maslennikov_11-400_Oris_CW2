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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers() throws Exception {
        UserDto userDto = new UserDto(1L, "Ivan", "coc");
        given(userService.findAll()).willReturn(Arrays.asList(userDto));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("user").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("Ivan"));
    }

    @Test
    void findByIdShouldReturnUserWhenExists() throws Exception {
        UserDto userDto = new UserDto(1L, "Ivan", "ivan@mail.com");
        given(userService.findById(1L)).willReturn(userDto);

        mockMvc.perform(get("/users/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("Ivan"));
    }

    @Test
    void findByIdShouldReturnNotFoundWhenMissing() throws Exception {
        given(userService.findById(404L)).willThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(get("/users/404").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturnCreatedUser() throws Exception {
        UserDto userDto = new UserDto(1L, "Ivan", "ivan@mail.com");
        given(userService.create(any(UserDto.class))).willReturn(userDto);

        mockMvc.perform(post("/users")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Ivan"));
    }

    @Test
    void updateShouldReturnUpdatedUserWhenExists() throws Exception {
        UserDto userDto = new UserDto(1L, "Ivan", "ivan@mail.com");
        given(userService.update(eq(1L), any(UserDto.class))).willReturn(userDto);

        mockMvc.perform(put("/users/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateShouldReturnNotFoundWhenMissing() throws Exception {
        UserDto userDto = new UserDto(2L, "Petr", "petr@mail.com");
        given(userService.update(eq(2L), any(UserDto.class))).willThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(put("/users/2")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenExists() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenMissing() throws Exception {
        doThrow(new NoSuchElementException("User not found")).when(userService).delete(404L);

        mockMvc.perform(delete("/users/404")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
