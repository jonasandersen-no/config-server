package com.bjoggis.config.controller;

import com.bjoggis.config.model.Properties;
import com.bjoggis.config.repository.PropertiesRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/properties")
public class PropertyController {

  private static final Logger log = LoggerFactory.getLogger(PropertyController.class);
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
    var propertiesI = repository.findAll();
    List<Properties> properties = new ArrayList<>();
    propertiesI.forEach(properties::add);
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

  @PostMapping("/import")
  // CSV formatted properties. New line separated, comma separated values.
  // application,label,name,profile,secret,value.
  ResponseEntity<Void> importProperties(@RequestParam("file") MultipartFile file)
      throws IOException {
    log.info("Importing properties from file: {}", file.getOriginalFilename());
    int count = 0;
    try (InputStream inputStream = file.getInputStream()) {
      try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
          while (reader.ready()) {
            String line = reader.readLine();
            String[] values = line.split(",");
            Properties properties = new Properties();
            properties.setApplication(values[0]);
            properties.setLabel(values[1]);
            properties.setName(values[2]);
            properties.setProfile(values[3]);
            properties.setSecret(Boolean.parseBoolean(values[4]));
            properties.setValue(values[5]);
            repository.save(properties);
            count++;
          }
        }
      }
    } finally {
      log.info("Imported {} properties", count);
    }

    return ResponseEntity.ok().build();
  }

}
