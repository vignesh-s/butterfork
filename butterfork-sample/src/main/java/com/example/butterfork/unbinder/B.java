package com.example.butterfork.unbinder;

import android.support.annotation.ColorInt;
import android.view.View;

import butterfork.BindColor;
import butterfork.ButterKnife;

public class B extends A {

  @BindColor("android.R.color.white") @ColorInt int whiteColor;

  public B(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
