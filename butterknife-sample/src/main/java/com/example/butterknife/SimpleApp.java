package com.example.butterfork;

import android.app.Application;
import butterfork.ButterKnife;

public class SimpleApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    ButterKnife.setDebug(BuildConfig.DEBUG);
  }
}
