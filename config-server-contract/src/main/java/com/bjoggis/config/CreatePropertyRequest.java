package com.bjoggis.config;

public record CreatePropertyRequest(
    String application,
    String profile,
    String label,
    String key,
    String value,
    Boolean secret
) {

  public CreatePropertyRequest {
    if (value.length() > 10000) {
      throw new IllegalArgumentException("Value is too long");
    }
  }
}
