package com.example.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

/** Settings: toggle for expiring-soon highlighting, stored in SharedPreferences. */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        Switch sw = findViewById(R.id.switchExpiry);
        sw.setChecked(prefs.getBoolean("expiry_alerts", true));
        sw.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("expiry_alerts", checked).apply());
    }
}
