package com.github.edivaldoramos.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageValidationToken {
  private String  message;
  private boolean isValid;
}
