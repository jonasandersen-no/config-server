package com.bjoggis.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CreatePropertyRequestTest {

  @Test
  void shouldThrowExceptionWhenValueIsTooLong() {
    assertThatThrownBy(
            () ->
                new CreatePropertyRequest(
                    "app", "profile", "label", "key", "a".repeat(10001), false))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Value is too long");
  }
}
