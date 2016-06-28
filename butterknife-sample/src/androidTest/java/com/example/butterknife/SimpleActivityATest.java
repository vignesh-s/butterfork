package com.example.butterfork;

import android.test.ActivityInstrumentationTestCase2;

public final class SimpleActivityATest extends ActivityInstrumentationTestCase2<SimpleActivity> {
  public SimpleActivityATest() {
    super(SimpleActivity.class);
  }

  public void testActivityStarts() {
    getActivity(); // Trigger activity creation.
    getInstrumentation().waitForIdleSync(); // Wait for it to complete startup.
  }
}
