package com.howtodoinjava.app.web;

import com.howtodoinjava.app.security.dto.AuthenticationRequest;
import com.howtodoinjava.app.security.dto.AuthenticationResponse;
import com.howtodoinjava.app.security.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(
      @RequestBody AuthenticationRequest authenticationRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
            authenticationRequest.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtTokenProvider.createToken(authentication);
    return ResponseEntity.ok(new AuthenticationResponse(jwt));
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logoutUser() {
    SecurityContextHolder.clearContext();
    return ResponseEntity.ok("Logout successful");
  }

}
