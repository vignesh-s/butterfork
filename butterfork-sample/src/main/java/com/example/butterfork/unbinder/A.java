package com.example.butterfork.unbinder;

import android.support.annotation.ColorInt;
import android.view.View;

import butterfork.BindColor;
import butterfork.ButterKnife;

public class A {

  @BindColor("android.R.color.black") @ColorInt int blackColor;

  public A(View view) {
    ButterKnife.bind(this, view);
  }
}
