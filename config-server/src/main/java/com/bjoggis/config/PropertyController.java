package com.bjoggis.config;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
class PropertyController {

  private final PropertiesRepository repository;

  PropertyController(PropertiesRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/{id}")
  ResponseEntity<PropertiesView> get(@PathVariable Long id) {
    Optional<Properties> properties = repository.findById(id);
    return properties
        .map(convertToPropertiesView())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/application/{application}")
  List<PropertiesView> findByApplication(@PathVariable String application) {
    return repository.findAllByApplication(application).stream()
        .map(convertToPropertiesView())
        .toList();
  }

  @GetMapping("/application/{application}/profile/{profile}")
  List<PropertiesView> findByApplicationAndProfile(
      @PathVariable String application, @PathVariable String profile) {
    return repository.findAllByApplicationAndProfile(application, profile).stream()
        .map(convertToPropertiesView())
        .toList();
  }

  @GetMapping("/key/{key}")
  List<PropertiesView> findByKey(@PathVariable String key) {
    return repository.findAllByKey(key).stream().map(convertToPropertiesView()).toList();
  }

  @GetMapping
  List<PropertiesView> findAll() {
    List<PropertiesView> list = repository.findAll().stream().map(convertToPropertiesView())
        .toList();
    return list;
  }

  @PostMapping
  PropertiesView create(@RequestBody CreatePropertyRequest properties) {
    Properties entity = new Properties();
    entity.setApplication(properties.application());
    entity.setProfile(properties.profile());
    entity.setLabel(properties.label());
    entity.setName(properties.key());
    entity.setValue(properties.value());
    entity.setSecret(properties.secret());
    Properties saved = repository.save(entity);
    return convertToPropertiesView().apply(saved);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!repository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    repository.deleteById(id);
    return ResponseEntity.ok().build();
  }

  private static Function<Properties, PropertiesView> convertToPropertiesView() {
    return property ->
        new PropertiesView(
            property.getId(),
            property.getApplication(),
            property.getProfile(),
            property.getName(),
            property.getValue(),
            property.isSecret());
  }
}
