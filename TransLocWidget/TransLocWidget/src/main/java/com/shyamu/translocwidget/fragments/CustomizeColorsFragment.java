package com.shyamu.translocwidget.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.shyamu.translocwidget.R;
import com.shyamu.translocwidget.bl.ArrivalTimeWidget;

/**
 * Created by Shyamal on 7/18/2015.
 */
public class CustomizeColorsFragment extends BaseFragment implements ColorPicker.OnColorChangedListener {

    private final String TAG = "CustomizeColorsFragment";
    private ColorPicker picker;
    private SVBar svBar;
    private OpacityBar opacityBar;
    private Button setBackgroundColorButton;
    private Button setTextColorButton;
    private LinearLayout currentBackgroundColor;
    private LinearLayout currentTextColor;

    private ArrivalTimeWidget atw;

    public CustomizeColorsFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_widget_list, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customize_colors, container, false);

        Bundle args = getArguments();
        if (args  != null && args.containsKey("atw")) {
            atw = (ArrivalTimeWidget) args.getSerializable("atw");
        } else {
            throw new IllegalStateException("No atw received from SelectStopFragment");
        }

        setHasOptionsMenu(true);
        picker = (ColorPicker) rootView.findViewById(R.id.picker);
        opacityBar = (OpacityBar) rootView.findViewById(R.id.opacitybar);
        svBar = (SVBar) rootView.findViewById(R.id.svbar);
        setBackgroundColorButton = (Button) rootView.findViewById(R.id.bChangeBackgroundColor);
        setTextColorButton = (Button) rootView.findViewById(R.id.bChangeTextColor);
        currentBackgroundColor = (LinearLayout) rootView.findViewById(R.id.llCurrentBackgroundColor);
        currentTextColor = (LinearLayout) rootView.findViewById(R.id.llCurrentTextColor);
        currentBackgroundColor.setBackgroundColor(atw.getBackgroundColor());
        currentTextColor.setBackgroundColor(atw.getTextColor());

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.setOnColorChangedListener(this);

        setBackgroundColorButton.setOnClickListener(v -> {
            picker.setOldCenterColor(picker.getColor());
            currentBackgroundColor.setBackgroundColor(picker.getColor());
            atw.setBackgroundColor(picker.getColor());
        });

        setTextColorButton.setOnClickListener(v -> {
            picker.setOldCenterColor(picker.getColor());
            currentTextColor.setBackgroundColor(picker.getColor());
            atw.setTextColor(picker.getColor());
        });

        currentBackgroundColor.setOnClickListener(v -> {
            ColorDrawable d = (ColorDrawable) currentBackgroundColor.getBackground();
            picker.setColor(d.getColor());
        });

        currentTextColor.setOnClickListener(v -> {
            ColorDrawable d = (ColorDrawable) currentTextColor.getBackground();
            picker.setColor(d.getColor());
        });

        return rootView;
    }

    @Override
    public void onColorChanged(int i) {

    }
}
