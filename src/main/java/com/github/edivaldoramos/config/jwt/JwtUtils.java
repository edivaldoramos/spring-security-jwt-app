package com.github.edivaldoramos.config.jwt;

import com.github.edivaldoramos.model.MessageValidationToken;
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
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtUtils {
  @Value("${app.expiration-ms}")
  private int expirationMs;
  @Value("${app.secret}")
  private String secret;

  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    return Jwts.builder().setSubject(userPrincipal.getUsername()).setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + expirationMs)).signWith(SignatureAlgorithm.HS512, secret).compact();
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
  }

  public String parseJwt(String token) {
    if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
      return token.substring(7);
    }
    return null;
  }

  public MessageValidationToken validateNameFromJwtToken(String token) {
    try {
      Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
      log.info("Valid token");
      return MessageValidationToken.builder()
          .isValid(true)
          .message("Valid token")
          .build();
    } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | ExpiredJwtException | IllegalArgumentException e) {
      log.error(e.getMessage());
      return MessageValidationToken.builder()
          .isValid(false)
          .message(e.getMessage())
          .build();
    }
  }
}
