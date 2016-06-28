package com.example.butterfork.unbinder;

import android.support.annotation.ColorInt;
import android.view.View;

import butterfork.BindColor;
import butterfork.ButterKnife;

public class E extends C {

  @BindColor("android.R.color.background_dark") @ColorInt int backgroundDarkColor;

  public E(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
