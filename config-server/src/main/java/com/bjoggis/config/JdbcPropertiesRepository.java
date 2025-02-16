package com.bjoggis.config;

import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

interface JdbcPropertiesRepository extends ListCrudRepository<Properties, Long> {

  List<Properties> findAllByApplicationAndProfile(String application, String profile);

  List<Properties> findAllByApplication(String application);

  List<Properties> findAllByNameContainingIgnoreCase(String name);
}
