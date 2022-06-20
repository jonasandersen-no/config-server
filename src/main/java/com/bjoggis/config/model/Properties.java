package com.bjoggis.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "studios_properties")
public class Properties {

  @Id
  @GeneratedValue
  private Long id;

  private String application;
  private String profile;
  private String label;
  private String name;
  private String value;
  private boolean secret;

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

  public boolean isSecret() {
    return secret;
  }

  public void setSecret(boolean secret) {
    this.secret = secret;
  }
}
