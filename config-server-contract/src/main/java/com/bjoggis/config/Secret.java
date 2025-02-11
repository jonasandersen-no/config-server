package com.bjoggis.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.Base64;

public final class Secret extends Value {

  public Secret(String value) {
    super(value);
    setValue(value);
  }

  @Override
  @JsonProperty("value")
  public String getValue() {
    return Base64.getEncoder().encodeToString(this.value.getBytes());
  }

  @Override
  @JsonSetter("value")
  public void setValue(String value) {
    this.value = new String(Base64.getDecoder().decode(value));
  }

  @Override
  public String toString() {
    if (this.value != null) {
      return "Secret{value=<present>}";
    }
    return "Secret{value=<absent>}";
  }
}
