package com.github.edivaldoramos.model.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {
  @NotBlank
  private String refreshToken;
}
