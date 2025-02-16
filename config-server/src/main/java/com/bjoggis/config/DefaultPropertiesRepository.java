package com.bjoggis.config;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class DefaultPropertiesRepository implements PropertiesRepository {

  private final JdbcPropertiesRepository delegate;

  DefaultPropertiesRepository(JdbcPropertiesRepository delegate) {
    this.delegate = delegate;
  }

  @Override
  public Properties save(Properties entity) {
    return delegate.save(entity);
  }

  @Override
  public Optional<Properties> findById(Long id) {
    return delegate.findById(id);
  }

  @Override
  public List<Properties> findAllByApplicationAndProfile(String application, String profile) {
    return delegate.findAllByApplicationAndProfile(application, profile);
  }

  @Override
  public List<Properties> findAll() {
    return delegate.findAll();
  }

  @Override
  public boolean existsById(Long id) {
    return delegate.existsById(id);
  }

  @Override
  public void deleteById(Long id) {
    delegate.deleteById(id);
  }

  @Override
  public List<Properties> findAllByApplication(String application) {
    return delegate.findAllByApplication(application);
  }

  @Override
  public List<Properties> findAllByKey(String key) {
    return delegate.findAllByNameContainingIgnoreCase(key);
  }
}
