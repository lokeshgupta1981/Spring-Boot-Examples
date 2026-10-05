package com.howtodoinjava.playlist;

import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class BrokenPagesController {

  // the template is songs/list.html, so "songs/lists" does not exist
  @GetMapping("/broken/missing")
  String missingTemplate() {
    return "songs/lists";
  }

  // the template reads song.titel instead of song.title
  @GetMapping("/broken/typo")
  String typo(Model model) {
    model.addAttribute("song",
        new Song(1, "Yellow", "Coldplay", 266, LocalDate.of(2000, 6, 26), 987654, "ROCK"));
    return "broken/typo";
  }

  // th:object accepts only the dollar form in Spring apps
  @GetMapping("/broken/selection")
  String selection(Model model) {
    model.addAttribute("song", yellow());
    return "broken/selection";
  }

  // inside th:object, *{tag} is looked up on the selected object
  @GetMapping("/broken/loop")
  String loop(Model model) {
    model.addAttribute("song", yellow());
    return "broken/loop";
  }

  private static Song yellow() {
    return new Song(1, "Yellow", "Coldplay", 266, LocalDate.of(2000, 6, 26), 987654, "ROCK,POP");
  }
}
