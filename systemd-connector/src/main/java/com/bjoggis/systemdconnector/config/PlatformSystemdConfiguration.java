package com.bjoggis.systemdconnector.config;
import com.bjoggis.systemdconnector.listener.PlatformServiceFailedNotification;
import com.bjoggis.systemdconnector.listener.PlatformServiceStartedNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformSystemdConfiguration {

  @Bean
  PlatformServiceFailedNotification platformServiceFailedNotification() {
    return new PlatformServiceFailedNotification();
  }

  @Bean
  PlatformServiceStartedNotification platformServiceStartedNotification() {
    return new PlatformServiceStartedNotification();
  }

}
