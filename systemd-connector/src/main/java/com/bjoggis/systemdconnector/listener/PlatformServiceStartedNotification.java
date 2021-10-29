package com.bjoggis.systemdconnector.listener;

import com.bjoggis.systemdconnector.SystemdNotify;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class PlatformServiceStartedNotification
    implements ApplicationListener<ApplicationReadyEvent> {

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    SystemdNotify.ready();
  }

}
