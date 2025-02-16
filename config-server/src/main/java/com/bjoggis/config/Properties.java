package com.bjoggis.config;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
class Properties {

  @Id
  private Long id;

  private String application = "application";
  private String profile = "profile";
  private String label;
  private String name;
  private String value;
  private Boolean secret;

  Properties() {
  }

  Properties(PropertiesSnapshot snapshot) {
    this.id = snapshot.id();
    this.application = snapshot.application();
    this.profile = snapshot.profile();
    this.label = snapshot.label();
    this.name = snapshot.name();
    this.value = snapshot.value();
    this.secret = snapshot.secret();
  }

  public Properties(String key, String value) {
    this.name = key;
    this.value = value;
  }

  public Properties(long id, String key, String value) {
    this.id = id;
    this.name = key;
    this.value = value;
  }

  public Properties(long id) {
    this.id = id;
  }

  public Properties(long id, String key, String value, String profile) {
    this.id = id;
    this.name = key;
    this.value = value;
    this.profile = profile;
  }

  public Properties(String key, String value, String profile) {
    this.name = key;
    this.value = value;
    this.profile = profile;
  }

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

  public PropertiesSnapshot snapshot() {
    return new PropertiesSnapshot(id, application, profile, label, name, value, secret);
  }

  public record PropertiesSnapshot(
      Long id,
      String application,
      String profile,
      String label,
      String name,
      String value,
      Boolean secret
  ) {

  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof Properties that)) {
      return false;
    }

    return Objects.equals(id, that.id) && Objects.equals(application, that.application)
           && Objects.equals(profile, that.profile) && Objects.equals(label, that.label)
           && Objects.equals(name, that.name) && Objects.equals(value, that.value)
           && Objects.equals(secret, that.secret);
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(id);
    result = 31 * result + Objects.hashCode(application);
    result = 31 * result + Objects.hashCode(profile);
    result = 31 * result + Objects.hashCode(label);
    result = 31 * result + Objects.hashCode(name);
    result = 31 * result + Objects.hashCode(value);
    result = 31 * result + Objects.hashCode(secret);
    return result;
  }

  @Override
  public String toString() {
    return "Properties{" +
           "id=" + id +
           ", application='" + application + '\'' +
           ", profile='" + profile + '\'' +
           ", label='" + label + '\'' +
           ", name='" + name + '\'' +
           ", value='" + value + '\'' +
           ", secret=" + secret +
           '}';
  }
}
