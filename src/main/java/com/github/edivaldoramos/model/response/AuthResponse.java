package com.github.edivaldoramos.model.response;

import java.util.List;
import lombok.Data;

@Data
public class AuthResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String refreshToken;
  private String email;
  private List<String> roles;

  public AuthResponse(String accessToken, String refreshToken, Long id, String username, String email, List<String> roles) {
    this.token = accessToken;
    this.refreshToken = refreshToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
  }
}
