package com.bjoggis.config;

public record Property(
    Long id,
    String application,
    String profile,
    String label,
    String name,
    String value,
    Boolean secret
) {

}
