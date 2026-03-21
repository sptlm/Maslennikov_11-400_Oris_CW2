package kfu.itis.maslennikov.service;

import kfu.itis.maslennikov.dto.NoteDto;
import kfu.itis.maslennikov.dto.NoteFormDto;
import kfu.itis.maslennikov.model.Note;
import kfu.itis.maslennikov.model.User;
import kfu.itis.maslennikov.repository.NoteRepository;
import kfu.itis.maslennikov.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Note> findOwnNotes(String username) {
        return noteRepository.findByAuthor(getUserByUsername(username)).stream()
                .sorted(Comparator.comparing(Note::getCreatedAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Note> findPublicNotes(String query) {
        if (query == null || query.isBlank()) {
            return noteRepository.findByIsPublicTrue().stream()
                    .sorted(Comparator.comparing(Note::getCreatedAt).reversed())
                    .toList();
        }
        return noteRepository.search(query).stream()
                .filter(Note::isPublic)
                .sorted(Comparator.comparing(Note::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public Note create(String username, NoteFormDto form) {
        Note note = new Note();
        note.setTitle(form.getTitle());
        note.setContent(form.getContent());
        note.setPublic(form.isPublic());
        note.setCreatedAt(LocalDateTime.now());
        note.setAuthor(getUserByUsername(username));
        return noteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public Note findOwnNote(Long id, String username) {
        Note note = findById(id);
        ensureOwnership(note, username);
        return note;
    }

    @Transactional
    public Note updateOwn(Long id, String username, NoteFormDto form) {
        Note note = findOwnNote(id, username);
        note.setTitle(form.getTitle());
        note.setContent(form.getContent());
        note.setPublic(form.isPublic());
        return noteRepository.save(note);
    }

    @Transactional
    public void deleteOwn(Long id, String username) {
        Note note = findOwnNote(id, username);
        noteRepository.delete(note);
    }

    @Transactional(readOnly = true)
    public List<NoteDto> findAllForAdmin() {
        return noteRepository.findAll().stream()
                .sorted(Comparator.comparing(Note::getCreatedAt).reversed())
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void deleteAny(Long id) {
        Note note = findById(id);
        noteRepository.delete(note);
    }

    @Transactional(readOnly = true)
    public NoteFormDto toFormDto(Note note) {
        NoteFormDto form = new NoteFormDto();
        form.setTitle(note.getTitle());
        form.setContent(note.getContent());
        form.setPublic(note.isPublic());
        return form;
    }

    private NoteDto toDto(Note note) {
        NoteDto dto = new NoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getCreatedAtDisplay());
        dto.setPublic(note.isPublic());
        dto.setAuthorId(note.getAuthor().getId());
        dto.setAuthorUsername(note.getAuthor().getUsername());
        return dto;
    }

    private Note findById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Note not found: " + id));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
    }

    private void ensureOwnership(Note note, String username) {
        if (!note.getAuthor().getUsername().equals(username)) {
            throw new SecurityException("Нельзя редактировать или удалять чужую заметку");
        }
    }
}
