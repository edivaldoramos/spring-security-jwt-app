package com.github.edivaldoramos.config.jwt;

import com.github.edivaldoramos.model.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtils {
  @Value("${app.secret}")
  private String secret;

  @Value("${app.expiration-ms}")
  private int expirationMs;

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser()
        .setSigningKey(secret)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public String generateJwtToken(Authentication authentication){
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    return Jwts.builder()
        .setSubject(userPrincipal.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + expirationMs))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public boolean validateNameFromJwtToken(String token) {
    try {
      Jwts.parser()
          .setSigningKey(secret)
          .parseClaimsJws(token);
      return true;
    }catch (SignatureException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }
}
