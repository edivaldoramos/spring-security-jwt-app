package com.github.edivaldoramos.model.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank
  private String username;
  @NotBlank
  private String password;
}
