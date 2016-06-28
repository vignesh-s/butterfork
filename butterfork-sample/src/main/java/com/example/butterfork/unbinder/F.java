package com.example.butterfork.unbinder;

import android.support.annotation.ColorInt;
import android.view.View;

import butterfork.BindColor;
import butterfork.ButterKnife;

public final class F extends D {

  @BindColor("android.R.color.background_light") @ColorInt int backgroundLightColor;

  public F(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
