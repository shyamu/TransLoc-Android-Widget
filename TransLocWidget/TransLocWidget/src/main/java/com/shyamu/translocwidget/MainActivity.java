package com.shyamu.translocwidget;

import android.app.Fragment;
import android.app.FragmentManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.shyamu.translocwidget.bl.ArrivalTimeWidget;
import com.shyamu.translocwidget.bl.Utils;
import com.shyamu.translocwidget.fragments.WidgetListFragment;
import com.shyamu.translocwidget.rest.model.TransLocArrival;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.shyamu.translocwidget.bl.Utils.TransLocDataType.ARRIVAL;


public class MainActivity extends AppCompatActivity implements WidgetListFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final String TRANSLOC_API_KEY= BuildConfig.TRANSLOC_API_KEY;
    private static ArrivalTimeWidget atw;
    private static int appWidgetId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // appWidgetId will equal widget being configured if we came from adding a widget to home screen
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .add(R.id.widget_container, new WidgetListFragment())
                    .addToBackStack(null)
                    .commit();
        }

        // Prevents user from pressing back to an empty activity
        getFragmentManager().addOnBackStackChangedListener(() -> {
            if (getFragmentManager().getBackStackEntryCount() == 0) finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.finish_item) {
            // Finish button from customize colors fragment
            Log.v(TAG, "Selected Finish");

            ArrayList<ArrivalTimeWidget> listViewArray;
            try {
                listViewArray = Utils.getArrivalTimeWidgetsFromStorage(this);
            } catch (IOException e) {
                Log.e(TAG, "Error in getting previous widget list", e);
                listViewArray = new ArrayList<>();
            }

            listViewArray.add(atw);

            try {
                Utils.writeArrivalTimeWidgetsToStorage(this, listViewArray);
            } catch (Exception e) {
                Log.e(TAG, "Error in writing widget list to storage");
                Log.e(TAG, e.getMessage());
            }
            if (appWidgetId > 0) {
                // configuration path so create widget
                getArrivalsFromServiceAndCreateWidget(atw);
            } else {
                // app path so return back to widget list
                FragmentManager fragmentManager = this.getFragmentManager();
                // clear back stack
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.widget_container, new WidgetListFragment())
                        .addToBackStack(null)
                        .commit();
                Toast.makeText(this, "Select Transloc Widget from your home screen widget drawer to see arrival times.", Toast.LENGTH_LONG).show();

            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    private void getArrivalsFromServiceAndCreateWidget(ArrivalTimeWidget atw) {
        Log.v(TAG, "creating widget: " + atw.toString());
        TransLocClient client =
                ServiceGenerator.createService(TransLocClient.class,
                        Utils.BASE_URL,
                        TRANSLOC_API_KEY,
                        atw.getAgencyID(),
                        ARRIVAL);
        client.arrivalEstimates(atw.getAgencyID(), atw.getRouteID(), atw.getStopID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((arrivals) -> {
                            handleWidgetCreation(arrivals, atw);
                        },
                        e -> handleArrivalTimeError(e)
                );
    }

    private void handleArrivalTimeError(Throwable e) {
        // TODO show relevant alert dialog
        Log.e(TAG, "error in getting arrival times", e);
    }

    private void handleWidgetCreation(List<TransLocArrival> arrivals, ArrivalTimeWidget atw) {
        if (arrivals != null) {
            // Calculate next arrival time
            if (arrivals.isEmpty()) {
                Log.d(TAG, "arrivals is empty");
                atw.setMinutesUntilArrival(-1);
            } else {
                TransLocArrival nextArrival = arrivals.get(0);
                int minsTillArrival = getMinsUntilArrival(nextArrival);
                atw.setMinutesUntilArrival(minsTillArrival);
            }

            // Create Widget
            RemoteViews remoteViews = Utils.createRemoteViews(this, atw, appWidgetId);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.updateAppWidget(appWidgetId, Utils.createRemoteViews(this, atw, appWidgetId));
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

            // Add appWidgetId to same ArrivalTimeWidget that is stored in device storage
            try {
                ArrayList<ArrivalTimeWidget> listOfWidgets = Utils.getArrivalTimeWidgetsFromStorage(this);
                for (int i = 0; i < listOfWidgets.size(); i++) {
                    ArrivalTimeWidget widget = listOfWidgets.get(i);
                    if (atw.equals(widget)) {
                        widget.setAppWidgetId(appWidgetId);
                        listOfWidgets.set(i, widget);
                        Utils.writeArrivalTimeWidgetsToStorage(this, listOfWidgets);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error in getting list of widgets from storage", e);
            }

            // Return result
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            Toast.makeText(this, "Tap on the widget to update!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.e(TAG, "arrivals is null!");
        }
    }

    private int getMinsUntilArrival(TransLocArrival arrival) {
        DateTime currentDate = new DateTime();
        DateTime arrivalDate = new DateTime(arrival.arrivalAt);
        return Utils.getMinutesBetweenTimes(currentDate, arrivalDate);
    }

    @Override
    public void onFragmentInteraction(ArrivalTimeWidget widget) {
        if (appWidgetId > 0) {
            getArrivalsFromServiceAndCreateWidget(widget);
        } else {
            Toast.makeText(getApplicationContext(), "Select Transloc Widget from your home screen widget drawer to see arrival times", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public static class CustomizeColorsFragment extends Fragment implements ColorPicker.OnColorChangedListener {

        private final String TAG = "CustomizeColorsFragment";
        private ColorPicker picker;
        private SVBar svBar;
        private OpacityBar opacityBar;
        private Button setBackgroundColorButton;
        private Button setTextColorButton;
        private LinearLayout currentBackgroundColor;
        private LinearLayout currentTextColor;

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
            if (args != null && args.containsKey("atw")) {
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
}
