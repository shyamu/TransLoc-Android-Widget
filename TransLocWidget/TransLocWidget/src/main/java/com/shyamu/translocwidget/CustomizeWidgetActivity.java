package com.shyamu.translocwidget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.shyamu.translocwidget.R;


public class CustomizeWidgetActivity extends Activity implements ColorPicker.OnColorChangedListener {

    private ColorPicker picker;
    private SVBar svBar;
    private OpacityBar opacityBar;
    private Button setBackgroundColorButton;
    private Button setTextColorButton;
    private LinearLayout currentBackgroundColor;
    private LinearLayout currentTextColor;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_widget);

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = settings.edit();

        picker = (ColorPicker) findViewById(R.id.picker);
        opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
        svBar = (SVBar) findViewById(R.id.svbar);
        setBackgroundColorButton = (Button) findViewById(R.id.bChangeBackgroundColor);
        setTextColorButton = (Button) findViewById(R.id.bChangeTextColor);
        currentBackgroundColor = (LinearLayout) findViewById(R.id.llCurrentBackgroundColor);
        currentTextColor = (LinearLayout) findViewById(R.id.llCurrentTextColor);

        // set current color views to what is currently saved.
        currentBackgroundColor.setBackgroundColor(settings.getInt("backgroundColor", 1996554497));
        currentTextColor.setBackgroundColor(settings.getInt("textColor", -1));

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.setOnColorChangedListener(this);

        setBackgroundColorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                picker.setOldCenterColor(picker.getColor());
                currentBackgroundColor.setBackgroundColor(picker.getColor());
                editor.putInt("backgroundColor", picker.getColor()).commit();
            }
        });

        setTextColorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                picker.setOldCenterColor(picker.getColor());
                currentTextColor.setBackgroundColor(picker.getColor());
                editor.putInt("textColor", picker.getColor()).commit();
            }
        });

        currentBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable d = (ColorDrawable) currentBackgroundColor.getBackground();
                picker.setColor(d.getColor());
            }
        });

        currentTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable d = (ColorDrawable) currentTextColor.getBackground();
                picker.setColor(d.getColor());
            }
        });

    }

    @Override
    public void onColorChanged(int i) {

    }
}