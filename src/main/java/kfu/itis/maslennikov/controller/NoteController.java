package kfu.itis.maslennikov.controller;

import kfu.itis.maslennikov.dto.NoteFormDto;
import kfu.itis.maslennikov.model.Note;
import kfu.itis.maslennikov.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public String ownNotes(Model model, Principal principal) {
        model.addAttribute("notes", noteService.findOwnNotes(principal.getName()));
        return "notes";
    }

    @GetMapping("/public")
    public String publicNotes(@RequestParam(name = "query", required = false) String query, Model model) {
        model.addAttribute("notes", noteService.findPublicNotes(query));
        model.addAttribute("query", query == null ? "" : query);
        return "public_notes";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("noteForm", new NoteFormDto());
        model.addAttribute("formAction", "/notes/create");
        model.addAttribute("formTitle", "Создание заметки");
        return "note_form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("noteForm") NoteFormDto noteFormDto, Principal principal) {
        noteService.create(principal.getName(), noteFormDto);
        return "redirect:/notes";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, Principal principal) {
        try {
            Note note = noteService.findOwnNote(id, principal.getName());
            model.addAttribute("noteForm", noteService.toFormDto(note));
            model.addAttribute("formAction", "/notes/" + id + "/edit");
            model.addAttribute("formTitle", "Редактирование заметки");
            model.addAttribute("noteId", id);
            return "note_form";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id,
                       @ModelAttribute("noteForm") NoteFormDto noteFormDto,
                       Principal principal) {
        try {
            noteService.updateOwn(id, principal.getName(), noteFormDto);
            return "redirect:/notes";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, Principal principal) {
        try {
            noteService.deleteOwn(id, principal.getName());
            return "redirect:/notes";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}