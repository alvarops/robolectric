package org.robolectric.shadows;

import android.os.HandlerThread;
import android.os.Looper;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.internal.ReflectionHelpers;

@Implements(HandlerThread.class)
public class ShadowHandlerThread {
  private Looper looper;

  @RealObject private HandlerThread realObject;

  public void __constructor__(String name) {
    __constructor__(name, -1);
  }

  @SuppressWarnings("UnusedParameters")
  public void __constructor__(String name, int priority) {
  }

  @Implementation
  public void run() {
    Looper.prepare();
    synchronized (realObject) {
      looper = Looper.myLooper();
      callOnLooperPrepared();
      realObject.notifyAll();
    }
    Looper.loop();
  }

  private void callOnLooperPrepared() {
    ReflectionHelpers.callInstanceMethodReflectively(realObject, "onLooperPrepared");
  }

  @Implementation
  public Looper getLooper() {
    if (!realObject.isAlive()) {
      return null;
    }

    // If the thread has been started, wait until the looper has been created.
    synchronized (realObject) {
      while (realObject.isAlive() && looper == null) {
        try {
          realObject.wait();
        } catch (InterruptedException ignored) {
        }
      }
    }
    return looper;
  }

  @Implementation
  public boolean quit() {
    Looper looper = getLooper();
    if (looper != null) {
      looper.quit();
      return true;
    }
    return false;
  }
 }
