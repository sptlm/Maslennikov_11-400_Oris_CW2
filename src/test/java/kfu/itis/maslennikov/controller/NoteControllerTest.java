package kfu.itis.maslennikov.controller;

import kfu.itis.maslennikov.dto.NoteFormDto;
import kfu.itis.maslennikov.model.Note;
import kfu.itis.maslennikov.model.User;
import kfu.itis.maslennikov.service.impl.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Test
    void ownNotesShouldReturnNotesView() throws Exception {
        given(noteService.findOwnNotes("ivan")).willReturn(List.of(buildNote()));

        mockMvc.perform(get("/notes").with(user("ivan").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"));
    }

    @Test
    void publicNotesShouldReturnPublicNotesViewWithQuery() throws Exception {
        given(noteService.findPublicNotes("spring")).willReturn(List.of(buildNote()));

        mockMvc.perform(get("/notes/public")
                        .param("query", "spring")
                        .with(user("ivan").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("public_notes"))
                .andExpect(model().attribute("query", "spring"))
                .andExpect(model().attributeExists("notes"));
    }

    @Test
    void createFormShouldReturnNoteFormView() throws Exception {
        mockMvc.perform(get("/notes/create").with(user("ivan").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attribute("formAction", "/notes/create"));
    }

    @Test
    void createShouldRedirectToOwnNotes() throws Exception {
        mockMvc.perform(post("/notes/create")
                        .with(user("ivan").roles("USER"))
                        .with(csrf())
                        .param("title", "My note")
                        .param("content", "My content")
                        .param("public", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));
    }

    @Test
    void editFormShouldReturnNoteFormWhenAccessible() throws Exception {
        Note note = buildNote();
        NoteFormDto formDto = new NoteFormDto();
        formDto.setTitle("old");

        given(noteService.findOwnNote(1L, "ivan")).willReturn(note);
        given(noteService.toFormDto(note)).willReturn(formDto);

        mockMvc.perform(get("/notes/1/edit").with(user("ivan").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attribute("noteId", 1L));
    }

    @Test
    void editFormShouldReturnNotFoundWhenNoteMissing() throws Exception {
        given(noteService.findOwnNote(404L, "ivan")).willThrow(new NoSuchElementException("missing"));

        mockMvc.perform(get("/notes/404/edit").with(user("ivan").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void editFormShouldReturnForbiddenWhenOwnershipViolation() throws Exception {
        given(noteService.findOwnNote(2L, "ivan")).willThrow(new SecurityException("forbidden"));

        mockMvc.perform(get("/notes/2/edit").with(user("ivan").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void editShouldRedirectWhenUpdateSucceeds() throws Exception {
        mockMvc.perform(post("/notes/1/edit")
                        .with(user("ivan").roles("USER"))
                        .with(csrf())
                        .param("title", "New title")
                        .param("content", "New content")
                        .param("public", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));
    }

    @Test
    void editShouldReturnNotFoundWhenNoteMissing() throws Exception {
        doThrow(new NoSuchElementException("missing"))
                .when(noteService).updateOwn(eq(404L), eq("ivan"), any(NoteFormDto.class));

        mockMvc.perform(post("/notes/404/edit")
                        .with(user("ivan").roles("USER"))
                        .with(csrf())
                        .param("title", "New title")
                        .param("content", "New content"))
                .andExpect(status().isNotFound());
    }

    @Test
    void editShouldReturnForbiddenWhenOwnershipViolation() throws Exception {
        doThrow(new SecurityException("forbidden"))
                .when(noteService).updateOwn(eq(2L), eq("ivan"), any(NoteFormDto.class));

        mockMvc.perform(post("/notes/2/edit")
                        .with(user("ivan").roles("USER"))
                        .with(csrf())
                        .param("title", "New title")
                        .param("content", "New content"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteShouldRedirectWhenDeleteSucceeds() throws Exception {
        mockMvc.perform(post("/notes/1/delete")
                        .with(user("ivan").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));
    }

    @Test
    void deleteShouldReturnNotFoundWhenNoteMissing() throws Exception {
        doThrow(new NoSuchElementException("missing")).when(noteService).deleteOwn(404L, "ivan");

        mockMvc.perform(post("/notes/404/delete")
                        .with(user("ivan").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnForbiddenWhenOwnershipViolation() throws Exception {
        doThrow(new SecurityException("forbidden")).when(noteService).deleteOwn(2L, "ivan");

        mockMvc.perform(post("/notes/2/delete")
                        .with(user("ivan").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    private Note buildNote() {
        User author = new User();
        author.setUsername("ivan");

        Note note = new Note();
        note.setId(1L);
        note.setTitle("Title");
        note.setContent("Content");
        note.setCreatedAt(LocalDateTime.now());
        note.setPublic(true);
        note.setAuthor(author);
        return note;
    }
}