package com.example.butterfork;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterfork.lib.SimpleAdapter;

import java.util.List;

import butterfork.Bind;
import butterfork.ButterFork;
import butterfork.OnClick;
import butterfork.OnItemClick;
import butterfork.OnLongClick;

import static android.widget.Toast.LENGTH_SHORT;

public class SimpleActivity extends Activity {
  private static final ButterFork.Action<View> ALPHA_FADE = new ButterFork.Action<View>() {
    @Override public void apply(View view, int index) {
      AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
      alphaAnimation.setFillBefore(true);
      alphaAnimation.setDuration(500);
      alphaAnimation.setStartOffset(index * 100);
      view.startAnimation(alphaAnimation);
    }
  };

  @Bind(B.id.title) TextView title;
  @Bind(B.id.subtitle) TextView subtitle;
  @Bind(B.id.hello) Button hello;
  @Bind(B.id.list_of_things) ListView listOfThings;
  @Bind(B.id.footer) TextView footer;

  @Bind({ B.id.title, B.id.subtitle, B.id.hello })
  List<View> headerViews;

  private SimpleAdapter adapter;

  @OnClick(B.id.hello) void sayHello() {
    Toast.makeText(this, "Hello, views!", LENGTH_SHORT).show();
    ButterFork.apply(headerViews, ALPHA_FADE);
  }

  @OnLongClick(B.id.hello) boolean sayGetOffMe() {
    Toast.makeText(this, "Let go of me!", LENGTH_SHORT).show();
    return true;
  }

  @OnItemClick(B.id.list_of_things) void onItemClick(int position) {
    Toast.makeText(this, "You clicked: " + adapter.getItem(position), LENGTH_SHORT).show();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    ButterFork.bind(this);

    // Contrived code to use the bound fields.
    title.setText("Butter Fork");
    subtitle.setText("Field and method binding for Android views.");
    footer.setText("by Jake Wharton");
    hello.setText("Say Hello");

    adapter = new SimpleAdapter(this);
    listOfThings.setAdapter(adapter);
  }
}
