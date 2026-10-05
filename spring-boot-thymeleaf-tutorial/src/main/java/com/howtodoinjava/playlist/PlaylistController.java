package com.howtodoinjava.playlist;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/songs")
public class PlaylistController {

  private final SongService songService;

  public PlaylistController(SongService songService) {
    this.songService = songService;
  }

  @GetMapping
  public String list(@RequestParam(required = false) String genre, Model model) {
    List<Song> songs = songService.findAll(genre);
    model.addAttribute("songs", songs);
    model.addAttribute("genre", genre);
    model.addAttribute("intro", "Songs for a <b>Friday</b> evening");
    return "songs/list";
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable long id, Model model) {
    Song song = songService.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No song " + id));
    model.addAttribute("song", song);
    return "songs/detail";
  }

  @GetMapping("/new")
  public String newSong(Model model) {
    model.addAttribute("song", new SongForm());
    return "songs/form";
  }

  @PostMapping
  public String create(@Valid @ModelAttribute("song") SongForm form, BindingResult result,
                       RedirectAttributes redirect) {
    if (result.hasErrors()) {
      return "songs/form";
    }
    Song saved = songService.add(form);
    redirect.addFlashAttribute("added", saved.title());
    return "redirect:/songs";
  }
}
