package com.example.butterfork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import butterfork.ButterKnife;

import static com.example.butterfork.SimpleAdapter.ViewHolder;
import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricTestRunner.class) //
@Config(manifest = "src/main/AndroidManifest.xml")
public class SimpleAdapterTest {
  @Test public void verifyViewHolderViews() {
    Context context = RuntimeEnvironment.application;

    View root = LayoutInflater.from(context).inflate(R.layout.simple_list_item, null);
    ViewHolder holder = new ViewHolder(root);

    assertThat(holder.word).hasId(R.id.word);
    assertThat(holder.length).hasId(R.id.length);
    assertThat(holder.position).hasId(R.id.position);

    ButterKnife.unbind(holder);
    assertThat(holder.word).isNull();
    assertThat(holder.length).isNull();
    assertThat(holder.position).isNull();
  }
}
