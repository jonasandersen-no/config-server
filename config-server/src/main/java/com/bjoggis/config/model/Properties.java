package com.bjoggis.config.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
public class Properties {

  @Id
  private Long id;

  private String application;
  private String profile;
  private String label;
  private String name;
  private String value;
  private Boolean secret;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getApplication() {
    return application;
  }

  public void setApplication(final String application) {
    this.application = application;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(final String profile) {
    this.profile = profile;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getName() {
    return name;
  }

  public void setName(final String key) {
    this.name = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public Boolean isSecret() {
    return secret;
  }

  public void setSecret(Boolean secret) {
    this.secret = secret;
  }
}
