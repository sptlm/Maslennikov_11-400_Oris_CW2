package kfu.itis.maslennikov.controller;

import kfu.itis.maslennikov.dto.NoteDto;
import kfu.itis.maslennikov.service.impl.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminNoteController.class)
class AdminNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Test
    void findAllShouldReturnNotesForAdmin() throws Exception {
        NoteDto noteDto = new NoteDto();
        noteDto.setId(1L);
        noteDto.setTitle("Title");
        given(noteService.findAllForAdmin()).willReturn(List.of(noteDto));

        mockMvc.perform(get("/admin/notes").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    void deleteShouldReturnNoContentWhenNoteExists() throws Exception {
        mockMvc.perform(delete("/admin/notes/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenNoteMissing() throws Exception {
        doThrow(new NoSuchElementException("Note not found")).when(noteService).deleteAny(404L);

        mockMvc.perform(delete("/admin/notes/404")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}