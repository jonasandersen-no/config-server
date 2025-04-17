package com.bjoggis.config;

import java.util.List;
import java.util.Optional;
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
  ResponseEntity<Properties> get(@PathVariable Long id) {
    Optional<Properties> properties = repository.findById(id);
    return properties.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/application/{application}")
  List<Properties> findByApplication(@PathVariable String application) {
    return repository.findAllByApplication(application);
  }

  @GetMapping("/application/{application}/profile/{profile}")
  List<Properties> findByApplicationAndProfile(
      @PathVariable String application, @PathVariable String profile) {
    return repository.findAllByApplicationAndProfile(application, profile);
  }

  @GetMapping("/key/{key}")
  List<Properties> findByKey(@PathVariable String key) {
    return repository.findAllByKey(key);
  }

  @GetMapping
  List<Properties> findAll() {
    return repository.findAll();
  }

  @PostMapping
  Properties create(@RequestBody CreatePropertyRequest properties) {
    Properties entity = new Properties();
    entity.setApplication(properties.application());
    entity.setProfile(properties.profile());
    entity.setLabel(properties.label());
    entity.setName(properties.key());
    entity.setValue(properties.value());
    entity.setSecret(properties.secret());
    return repository.save(entity);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!repository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    repository.deleteById(id);
    return ResponseEntity.ok().build();
  }
}
