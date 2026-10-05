package com.howtodoinjava.playlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(PlaylistController.class)
@Import(SongService.class)
class PlaylistControllerTest {

  @Autowired
  MockMvcTester mvc;

  @Autowired
  MockMvc mockMvc;

  @Test
  void listShowsViewNameModelAndRows() {
    MvcTestResult result = mvc.get().uri("/songs").exchange();

    assertThat(result).hasStatusOk().hasViewName("songs/list");
    assertThat(result).model().containsKeys("songs", "intro");
    assertThat(result).model().extractingByKey("songs")
        .asInstanceOf(LIST).hasSize(3);
    assertThat(result).bodyText()
        .contains("<h1>My playlist (3 songs)</h1>")
        .contains("<a href=\"/songs/2\">Yellow</a>")
        .contains("<td>26 Jun 2000</td>")
        .contains("<td>4:26</td>")
        .contains("<td>4,500,000</td>")
        .contains("<tr class=\"odd\">");
  }

  @Test
  void thTextEscapesAndThUtextDoesNot() {
    assertThat(mvc.get().uri("/songs")).bodyText()
        .contains("<p class=\"intro\">Songs for a <b>Friday</b> evening</p>")
        .contains("<p class=\"intro-escaped\">Songs for a &lt;b&gt;Friday&lt;/b&gt; evening</p>");
  }

  @Test
  void genreParameterFiltersTheList() {
    MvcTestResult result = mvc.get().uri("/songs?genre=JAZZ").exchange();
    assertThat(result).hasStatusOk();
    assertThat(result).bodyText()
        .contains("My playlist (1 songs)")
        .contains("<a href=\"/songs?genre=JAZZ\">Jazz</a>")
        .contains("<span class=\"tag jazz\">Jazz</span>")
        .doesNotContain("Coldplay");
  }

  @Test
  void unknownGenreShowsEmptyMessage() {
    assertThat(mvc.get().uri("/songs?genre=metal")).bodyText()
        .contains("No songs match this filter.")
        .doesNotContain("<table>");
  }

  @Test
  void detailUsesSelectedObject() {
    MvcTestResult result = mvc.get().uri("/songs/1").exchange();
    assertThat(result).hasStatusOk().hasViewName("songs/detail");
    assertThat(result).bodyText()
        .contains("<title>So What</title>")
        .contains("<p>by Miles Davis</p>")
        .contains("<p>August 17, 1959</p>")
        .contains("<a href=\"/songs?genre=JAZZ\">More JAZZ songs</a>");
  }

  @Test
  void unknownSongReturns404() {
    assertThat(mvc.get().uri("/songs/99")).hasStatus(404);
  }

  @Test
  void germanLocaleUsesGermanMessages() {
    assertThat(mvc.get().uri("/songs").header("Accept-Language", "de")).bodyText()
        .contains("<h1>Meine Playlist (3 Lieder)</h1>")
        .contains("<th>Wiedergaben</th>");
  }

  @Test
  void invalidFormReturnsFormWithErrors() {
    MvcTestResult result = mvc.post().uri("/songs")
        .param("title", " ")
        .param("artist", "Adele")
        .param("durationSeconds", "5")
        .param("released", "2099-01-01")
        .param("genre", "POP")
        .exchange();

    assertThat(result).hasStatusOk().hasViewName("songs/form");
    assertThat(result).model().extractingBindingResult("song")
        .hasErrorsCount(3)
        .hasFieldErrors("title", "durationSeconds", "released");
    assertThat(result).bodyText()
        .contains("Please fix 3 errors.")
        .contains("<span class=\"error\">Please enter a song title.</span>")
        .contains("<span class=\"error\">must be greater than or equal to 30</span>")
        .contains("<span class=\"error\">must be a date in the past or in the present</span>")
        .contains("class=\"invalid\"")
        .contains("value=\"Adele\"");
  }

  @Test
  @DirtiesContext
  void validFormRedirectsWithFlashAttribute() {
    MvcTestResult result = mvc.post().uri("/songs")
        .param("title", "Hello")
        .param("artist", "Adele")
        .param("durationSeconds", "295")
        .param("released", "2015-10-23")
        .param("genre", "POP")
        .exchange();

    assertThat(result).hasStatus3xxRedirection().hasRedirectedUrl("/songs");
    assertThat(result).flash().containsEntry("added", "Hello");
  }

  @Test
  void classicMockMvcStyle() throws Exception {
    mockMvc.perform(get("/songs"))
        .andExpect(status().isOk())
        .andExpect(view().name("songs/list"))
        .andExpect(model().attribute("songs", hasSize(3)))
        .andExpect(content().string(containsString("Blinding Lights")));

    mockMvc.perform(post("/songs").param("title", "").param("artist", "Adele")
            .param("durationSeconds", "200").param("released", "2015-10-23").param("genre", "POP"))
        .andExpect(view().name("songs/form"))
        .andExpect(model().attributeHasFieldErrorCode("song", "title", "NotBlank"));
  }
}
