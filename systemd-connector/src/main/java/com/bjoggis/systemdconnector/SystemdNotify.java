package com.bjoggis.systemdconnector;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * All credits, https://gist.github.com/juur/048cc3d0554953b717e9c6867970f30e.
 */
public class SystemdNotify {

  interface SystemD extends Library {

    SystemD INSTANCE = Native.load(SYSTEMD_SO, SystemD.class);

    int sd_notify(int unsetEnvironment, String state);
  }

  private static final String SYSTEMD_SO = "systemd";
  private static final String READY = "READY=1";
  private static final String RELOADING = "RELOADING=1";
  private static final String STOPPING = "STOPPING=1";
  private static final String STATUS = "STATUS=%s";
  private static final String WATCHDOG = "WATCHDOG=1";
  private static final String MAINPID = "MAINPID=%d";
  private static final String ERRNO = "ERRNO=%d";
  private static final String BUSERROR = "BUSERROR=%s";
  private static String osType;

  public static void busError(final String error) {
    notify(String.format(BUSERROR, error));
  }

  public static void errno(final int errno) {
    notify(String.format(ERRNO, errno));
  }

  public static void mainPid(final long pid) {
    notify(String.format(MAINPID, pid));
  }

  /**
   * notify.
   */
  public static void notify(final String message) {

    if (!osType().startsWith("linux")) {
      return;
    }

    try {
      final int returnCode = SystemD.INSTANCE.sd_notify(0, message);

      if (returnCode < 0) {
        throw new RuntimeException(String.format("sd_notify returned %d", returnCode));
      }

    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void ready() {
    notify(READY);
  }

  public static void reloading() {
    notify(RELOADING);
  }

  public static void status(final String text) {
    notify(String.format(STATUS, text));
  }

  public static void stopping() {
    notify(STOPPING);
  }

  public static void watchdog() {
    notify(WATCHDOG);
  }

  private static synchronized String osType() {
    if (osType == null) {
      osType = System.getProperty("os.name").toLowerCase();
    }

    return osType;
  }

  private SystemdNotify() {
    throw new RuntimeException("This class should not be instantiated");
  }
}
