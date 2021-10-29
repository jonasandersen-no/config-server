package com.bjoggis.systemdconnector.listener;

import com.bjoggis.systemdconnector.SystemdNotify;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

public class PlatformServiceFailedNotification
    implements ApplicationListener<ApplicationFailedEvent> {

  @Override
  public void onApplicationEvent(ApplicationFailedEvent event) {
    SystemdNotify.errno(99);
  }

}
