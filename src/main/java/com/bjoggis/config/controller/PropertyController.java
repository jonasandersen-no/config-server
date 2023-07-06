package com.bjoggis.config.controller;

import com.bjoggis.config.model.Properties;
import com.bjoggis.config.repository.PropertiesRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/properties")
public class PropertyController {

  private final PropertiesRepository repository;

  public PropertyController(PropertiesRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/{id}")
  ResponseEntity<Properties> get(@PathVariable Long id) {
    Optional<Properties> properties = repository.findById(id);

    return properties.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }


  @GetMapping
  List<Properties> list(@RequestParam(required = false) String application,
      @RequestParam(required = false) String key,
      @RequestParam(required = false) String profile) {
    Properties searchBy = new Properties();
    if (StringUtils.hasText(application)) {
      searchBy.setApplication(application);
    }
    if (StringUtils.hasText(key)) {
      searchBy.setName(key);
    }
    if (StringUtils.hasText(profile)) {
      searchBy.setProfile(profile);
    }
    final Example<Properties> example = Example.of(searchBy);
    final List<Properties> properties = repository.findAll(example);
    properties.sort(Comparator.comparing(Properties::getId));
    return properties;
  }

  @PostMapping
  Properties create(@RequestBody Properties properties) {
    return repository.save(properties);
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
