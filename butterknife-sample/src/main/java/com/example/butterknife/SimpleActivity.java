package com.example.butterknife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterfork.SimpleLibActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnLongClick;

import static android.widget.Toast.LENGTH_SHORT;

public class SimpleActivity extends Activity {
  private static final ButterKnife.Action<View> ALPHA_FADE = new ButterKnife.Action<View>() {
    @Override public void apply(@NonNull View view, int index) {
      AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
      alphaAnimation.setFillBefore(true);
      alphaAnimation.setDuration(500);
      alphaAnimation.setStartOffset(index * 100);
      view.startAnimation(alphaAnimation);
    }
  };

  @BindView(B.id.title) TextView title;
  @BindView(B.id.subtitle) TextView subtitle;
  @BindView(B.id.hello) Button hello;
  @BindView(B.id.library) Button library;
  @BindView(B.id.list_of_things) ListView listOfThings;
  @BindView(B.id.footer) TextView footer;

  @BindViews({ B.id.title, B.id.subtitle, B.id.hello }) List<View> headerViews;

  private SimpleAdapter adapter;

  @OnClick(B.id.hello) void sayHello() {
    Toast.makeText(this, "Hello, views!", LENGTH_SHORT).show();
    ButterKnife.apply(headerViews, ALPHA_FADE);
  }

  @OnClick(B.id.library) void gotToLib() {
    startActivity(new Intent(this, SimpleLibActivity.class));
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
    ButterKnife.bind(this);

    // Contrived code to use the bound fields.
    title.setText("Butter Knife");
    subtitle.setText("Field and method binding for Android views.");
    footer.setText("by Jake Wharton");
    hello.setText("Say Hello");
    library.setText("Butter Fork Sample");

    adapter = new SimpleAdapter(this);
    listOfThings.setAdapter(adapter);
  }
}
