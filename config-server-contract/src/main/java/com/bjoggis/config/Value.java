package com.bjoggis.config;

public sealed class Value permits Secret {

  protected String value;

  public Value(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
