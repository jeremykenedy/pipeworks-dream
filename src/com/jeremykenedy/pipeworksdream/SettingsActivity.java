package com.jeremykenedy.pipeworksdream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public final class SettingsActivity extends Activity {
  private SharedPreferences preferences;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    ScrollView scroll = new ScrollView(this);
    LinearLayout content = new LinearLayout(this);
    content.setOrientation(LinearLayout.VERTICAL);
    content.setPadding(56, 32, 56, 32);
    scroll.addView(content);
    TextView title = new TextView(this);
    title.setText("Pipeworks Dream settings");
    title.setTextSize(28);
    title.setPadding(0, 0, 0, 20);
    content.addView(title);
    Button preview = new Button(this);
    preview.setText("Preview animation");
    preview.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            startActivity(new Intent(SettingsActivity.this, PreviewActivity.class));
          }
        });
    content.addView(preview);
    addChoice(
        content,
        "Background",
        "background",
        new String[] {"midnight", "ocean", "graphite", "random"},
        new String[] {"Midnight", "Deep ocean", "Graphite", "Random"},
        "midnight");
    addChoice(
        content,
        "Pipe count",
        "density",
        new String[] {"few", "handful", "many", "ton", "schools", "random"},
        new String[] {"A few", "A handful", "Many", "A ton", "Schools", "Random"},
        "handful");
    addChoice(
        content,
        "Growth speed",
        "speed",
        new String[] {"slow", "natural", "quick", "random"},
        new String[] {"Slow", "Natural", "Quick", "Random"},
        "natural");
    addChoice(
        content,
        "Pipe finish",
        "palette",
        new String[] {"copper", "cool", "neon", "monochrome", "random"},
        new String[] {"Copper", "Cool steel", "Neon", "Monochrome", "Random"},
        "copper");
    addChoice(
        content,
        "Lighting",
        "lighting",
        new String[] {"subtle", "cinematic", "bright", "random"},
        new String[] {"Subtle", "Cinematic", "Bright", "Random"},
        "cinematic");
    addChoice(
        content,
        "Pipe glow",
        "glow",
        new String[] {"on", "off", "random"},
        new String[] {"On", "Off", "Random"},
        "on");
    CheckBox randomize = new CheckBox(this);
    randomize.setText("Randomize all settings each time the screensaver starts");
    randomize.setTextSize(18);
    randomize.setChecked(preferences.getBoolean("randomize_all", false));
    randomize.setOnCheckedChangeListener(
        new android.widget.CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(android.widget.CompoundButton button, boolean checked) {
            preferences.edit().putBoolean("randomize_all", checked).apply();
          }
        });
    content.addView(randomize);
    setContentView(scroll);
  }

  private void addChoice(
      LinearLayout content,
      String title,
      String key,
      String[] values,
      String[] labels,
      String fallback) {
    TextView label = new TextView(this);
    label.setText(title);
    label.setTextSize(18);
    label.setPadding(0, 16, 0, 4);
    content.addView(label);
    Spinner spinner = new Spinner(this);
    spinner.setFocusable(true);
    spinner.setFocusableInTouchMode(true);
    spinner.setGravity(Gravity.CENTER_VERTICAL);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    String selected = preferences.getString(key, fallback);
    int selection = 0;
    for (int i = 0; i < values.length; i++) if (values[i].equals(selected)) selection = i;
    spinner.setSelection(selection);
    spinner.setOnItemSelectedListener(
        new android.widget.AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(
              android.widget.AdapterView<?> parent, View view, int position, long id) {
            preferences.edit().putString(key, values[position]).apply();
          }

          @Override
          public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    content.addView(spinner);
  }
}
