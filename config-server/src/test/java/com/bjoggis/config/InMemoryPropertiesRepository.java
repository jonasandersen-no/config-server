package com.bjoggis.config;

import com.bjoggis.config.Properties.PropertiesSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
class InMemoryPropertiesRepository implements PropertiesRepository {

  private final Map<Long, PropertiesSnapshot> idToProperties = new HashMap<>();
  private final Map<ApplicationAndProfile, List<PropertiesSnapshot>> applicationAndProfileToProperties = new HashMap<>();
  private final Map<String, List<PropertiesSnapshot>> applicationToProperties = new HashMap<>();
  private final Map<String, List<PropertiesSnapshot>> keyToProperties = new HashMap<>();
  private final AtomicLong sequence = new AtomicLong();

  @Override
  public Properties save(Properties entity) {
    PropertiesSnapshot snapshot = entity.snapshot();
    if (snapshot.id() == null) {
      long id = sequence.incrementAndGet();
      Properties copy = new Properties(snapshot);
      copy.setId(id);
      snapshot = copy.snapshot();
    }
    idToProperties.put(snapshot.id(), snapshot);
    saveToKey(snapshot);
    saveToApplication(snapshot);
    saveToApplicationAndProfile(snapshot);
    return new Properties(snapshot);
  }

  @Override
  public Optional<Properties> findById(Long id) {
    PropertiesSnapshot snapshot = idToProperties.get(id);
    if (snapshot == null) {
      return Optional.empty();
    }

    return Optional.of(new Properties(snapshot));
  }

  @Override
  public List<Properties> findAllByApplicationAndProfile(String application, String profile) {
    return applicationAndProfileToProperties.get(new ApplicationAndProfile(application, profile)).stream()
        .map(Properties::new)
        .toList();
  }

  @Override
  public List<Properties> findAll() {
    return idToProperties.values()
        .stream()
        .map(Properties::new)
        .toList();
  }

  @Override
  public boolean existsById(Long id) {
    return idToProperties.containsKey(id);
  }

  @Override
  public void deleteById(Long id) {
    idToProperties.remove(id);
  }

  @Override
  public List<Properties> findAllByApplication(String application) {
    return applicationToProperties.get(application).stream()
        .map(Properties::new)
        .toList();
  }

  @Override
  public List<Properties> findAllByKey(String key) {
    List<String> keys = keyToProperties.keySet().stream()
        .filter(s -> s.toUpperCase().contains(key.toUpperCase()))
        .toList();

    List<Properties> result = new ArrayList<>();
    for (String foundKey : keys) {
      result.addAll(keyToProperties.get(foundKey).stream()
          .map(Properties::new)
          .toList());
    }
    return result;
  }

  private void saveToKey(PropertiesSnapshot snapshot) {
    if (snapshot.name() != null) {
      String key = snapshot.name();
      if (keyToProperties.containsKey(key)) {
        keyToProperties.get(key).add(snapshot);
      } else {
        List<PropertiesSnapshot> values = new ArrayList<>();
        values.add(snapshot);
        keyToProperties.put(key, values);
      }
    }
  }

  private void saveToApplication(PropertiesSnapshot snapshot) {
    if (applicationToProperties.containsKey(snapshot.application())) {
      applicationToProperties.get(snapshot.application()).add(snapshot);
    } else {
      List<PropertiesSnapshot> values = new ArrayList<>();
      values.add(snapshot);
      applicationToProperties.put(snapshot.application(), values);
    }
  }

  private void saveToApplicationAndProfile(PropertiesSnapshot snapshot) {
    ApplicationAndProfile key = new ApplicationAndProfile(snapshot.application(), snapshot.profile());
    if (applicationAndProfileToProperties.containsKey(key)) {
      applicationAndProfileToProperties.get(key).add(snapshot);
    } else {
      List<PropertiesSnapshot> values = new ArrayList<>();
      values.add(snapshot);
      applicationAndProfileToProperties.put(key, values);
    }
  }

  record ApplicationAndProfile(String application, String profile) {

  }
}
