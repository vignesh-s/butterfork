package com.example.butterfork.unbinder;

import android.support.annotation.ColorInt;
import android.view.View;

import butterfork.BindColor;
import butterfork.ButterKnife;

public class D extends C {

  @BindColor("android.R.color.darker_gray") @ColorInt int grayColor;

  public D(View view) {
    super(view);
    ButterKnife.bind(this, view);
  }
}
