package com.bjoggis.configserver;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "properties")
public class Properties {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String application;
  private String profile;
  private String label;
  private String key;
  private String value;

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

  public String getKey() {
    return key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
