package com.howtodoinjava.app.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

  Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  public String createToken(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + 3600000);

    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS512, key)
        .compact();
  }


  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }


  public boolean validateToken(String token) {
    // Check if the token is valid and not expired
    try {
      Jwts.parser().setSigningKey(key).parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty");
    } catch (SignatureException e) {
      log.error("there is an error with the signature of you token ");
    }
    return false;
  }

  public String getUsername(String token) {
    // Extract the username from the JWT token
    return Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }
}