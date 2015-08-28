package com.example.butterfork;

import android.app.Application;

import butterfork.ButterFork;

public class SimpleApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    ButterFork.setDebug(BuildConfig.DEBUG);
  }
}
