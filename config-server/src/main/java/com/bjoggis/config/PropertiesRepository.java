package com.bjoggis.config;

import java.util.List;
import java.util.Optional;

interface PropertiesRepository {

  Properties save(Properties entity);

  Optional<Properties> findById(Long id);

  List<Properties> findAll();

  List<Properties> findAllByApplicationAndProfile(String application, String profile);

  boolean existsById(Long id);

  void deleteById(Long id);

  List<Properties> findAllByApplication(String application);

  List<Properties> findAllByKey(String key);
}
