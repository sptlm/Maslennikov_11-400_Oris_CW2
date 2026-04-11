package kfu.itis.maslennikov.service;

import kfu.itis.maslennikov.dto.NoteDto;
import kfu.itis.maslennikov.dto.NoteFormDto;
import kfu.itis.maslennikov.model.Note;
import kfu.itis.maslennikov.model.User;
import kfu.itis.maslennikov.repository.NoteRepository;
import kfu.itis.maslennikov.repository.UserRepository;
import kfu.itis.maslennikov.service.impl.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteService noteService;

    private User author;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("alice");
        author.setEmail("alice@mail.com");
    }

    @Test
    void findOwnNotesShouldReturnSortedByDateDesc() {
        Note older = note("old", LocalDateTime.now().minusDays(1), true, author);
        Note newer = note("new", LocalDateTime.now(), false, author);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(author));
        when(noteRepository.findByAuthor(author)).thenReturn(List.of(older, newer));

        List<Note> result = noteService.findOwnNotes("alice");

        assertThat(result).extracting(Note::getTitle).containsExactly("new", "old");
    }

    @Test
    void findPublicNotesShouldHandleBlankAndSearchFlow() {
        Note publicOld = note("public1", LocalDateTime.now().minusHours(2), true, author);
        Note publicNew = note("public2", LocalDateTime.now().minusHours(1), true, author);
        when(noteRepository.findByIsPublicTrue()).thenReturn(List.of(publicOld, publicNew));

        List<Note> blankQueryResult = noteService.findPublicNotes(" ");
        assertThat(blankQueryResult).extracting(Note::getTitle).containsExactly("public2", "public1");

        Note privateNote = note("private", LocalDateTime.now().plusHours(1), false, author);
        when(noteRepository.search("term")).thenReturn(List.of(publicOld, privateNote, publicNew));

        List<Note> searchedResult = noteService.findPublicNotes("term");
        assertThat(searchedResult).extracting(Note::getTitle).containsExactly("public2", "public1");
    }

    @Test
    void createShouldPopulateAndPersistNote() {
        NoteFormDto form = new NoteFormDto();
        form.setTitle("t");
        form.setContent("c");
        form.setPublic(true);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(author));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note saved = noteService.create("alice", form);

        assertThat(saved.getTitle()).isEqualTo("t");
        assertThat(saved.getContent()).isEqualTo("c");
        assertThat(saved.isPublic()).isTrue();
        assertThat(saved.getAuthor()).isEqualTo(author);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findOwnNoteShouldThrowForMissingNoteAndForeignOwnership() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> noteService.findOwnNote(99L, "alice"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Note not found");

        User another = new User();
        another.setUsername("bob");
        Note foreign = note("x", LocalDateTime.now(), true, another);
        when(noteRepository.findById(2L)).thenReturn(Optional.of(foreign));

        assertThatThrownBy(() -> noteService.findOwnNote(2L, "alice"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void updateOwnAndDeleteOwnShouldUseOwnershipAndRepositoryOperations() {
        Note owned = note("old", LocalDateTime.now(), true, author);
        owned.setId(10L);
        when(noteRepository.findById(10L)).thenReturn(Optional.of(owned));
        when(noteRepository.save(owned)).thenReturn(owned);
        doNothing().when(noteRepository).delete(owned);

        NoteFormDto updateForm = new NoteFormDto();
        updateForm.setTitle("new");
        updateForm.setContent("updated");
        updateForm.setPublic(false);

        Note updated = noteService.updateOwn(10L, "alice", updateForm);
        assertThat(updated.getTitle()).isEqualTo("new");
        assertThat(updated.getContent()).isEqualTo("updated");
        assertThat(updated.isPublic()).isFalse();

        noteService.deleteOwn(10L, "alice");
        verify(noteRepository).delete(owned);
    }

    @Test
    void findAllForAdminDeleteAnyAndToFormDtoShouldWork() {
        Note note = note("admin", LocalDateTime.now(), true, author);
        note.setId(7L);

        when(noteRepository.findAll()).thenReturn(List.of(note));
        when(noteRepository.findById(7L)).thenReturn(Optional.of(note));

        List<NoteDto> dtos = noteService.findAllForAdmin();
        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getAuthorUsername()).isEqualTo("alice");
        assertThat(dtos.get(0).getCreatedAt()).isNotBlank();

        NoteFormDto form = noteService.toFormDto(note);
        assertThat(form.getTitle()).isEqualTo("admin");
        assertThat(form.getContent()).isEqualTo(note.getContent());
        assertThat(form.isPublic()).isTrue();

        noteService.deleteAny(7L);
        verify(noteRepository).delete(note);

        when(noteRepository.findById(8L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> noteService.deleteAny(8L)).isInstanceOf(NoSuchElementException.class);
    }

    private static Note note(String title, LocalDateTime createdAt, boolean isPublic, User author) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(title + " content");
        note.setCreatedAt(createdAt);
        note.setPublic(isPublic);
        note.setAuthor(author);
        return note;
    }
}