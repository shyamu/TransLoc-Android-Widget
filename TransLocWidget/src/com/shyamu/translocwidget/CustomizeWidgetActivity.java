package com.shyamu.translocwidget;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;


public class CustomizeWidgetActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.color_prefs);
        Toast.makeText(CustomizeWidgetActivity.this,"Go 'Back' to save",Toast.LENGTH_LONG).show();

    }
}