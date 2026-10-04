package com.howtodoinjava.security;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Three kinds of endpoints: public, for any logged-in user, and for admins only.
 */
@RestController
public class PlaylistController {

  @GetMapping("/public/top-songs")
  public String topSongs() {
    return "Top songs: Yellow, Halo, Hello";
  }

  @GetMapping("/playlists")
  public String myPlaylists(Principal principal) {
    return "Playlists of " + principal.getName() + ": Morning Run, Focus";
  }

  @PostMapping("/playlists")
  public String createPlaylist(@RequestBody String name, Principal principal) {
    return "Created playlist '" + name + "' for " + principal.getName();
  }

  @GetMapping("/admin/report")
  public String report() {
    return "Users: 2, Playlists: 4";
  }
}
