package com.bjoggis.config.repository;

import com.bjoggis.config.model.Properties;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertiesRepository extends JpaRepository<Properties, Long> {

}
