package com.shyamu.translocwidget.widget;

import android.app.Activity;
import android.app.ActivityOptions;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.shyamu.translocwidget.MainActivity;
import com.shyamu.translocwidget.R;
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


public class ConfigurationActivity extends AppCompatActivity implements WidgetListFragment.OnFragmentInteractionListener {

    private int appWidgetId = 0;

    private static final String TAG = "ConfigurationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_list);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        /*
        addNewWidgetButton = (FloatingActionButton) findViewById(R.id.fabAddNewWidget);
        addNewWidgetButton.setVisibility(View.VISIBLE);
        addNewWidgetButton.setOnClickListener(v -> {
            Transition exitTrans = new Explode();
            getWindow().setExitTransition(exitTrans);
            Transition reenterTrans = new Explode();
            getWindow().setReenterTransition(reenterTrans);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ConfigurationActivity.this);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("starting_fragment", "AddAgencyFragment");
            startActivityForResult(intent, 100, options.toBundle());
        });
        */
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.widget_container, new WidgetListFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        if(addNewWidgetButton == null) addNewWidgetButton = (FloatingActionButton) findViewById(R.id.fabAddNewWidget);
        addNewWidgetButton.setVisibility(View.VISIBLE);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_widget_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(ArrivalTimeWidget incomingAtw) {
        Log.d(TAG, "inOnFragmentInteraction");
        getArrivalsFromServiceAndCreateWidget(incomingAtw);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult");
        if(requestCode == 100) {
            if(resultCode == 1) {
                ArrivalTimeWidget atw = (ArrivalTimeWidget) data.getSerializableExtra("atw");
                getArrivalsFromServiceAndCreateWidget(atw);
            } else {
                // error
            }
        }
    }

    private void getArrivalsFromServiceAndCreateWidget(ArrivalTimeWidget atw) {
        Log.v(TAG, "creating widget: " + atw.toString());
        TransLocClient client =
                ServiceGenerator.createService(TransLocClient.class,
                        Utils.BASE_URL,
                        this.getString(R.string.mashape_key),
                        atw.getAgencyID(),
                        ARRIVAL);
        client.arrivalEstimates(atw.getAgencyID(), atw.getRouteID(), atw.getStopID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((arrivals) -> {
                            handleWidgetCreation(arrivals, atw);
                        },
                        e -> handleServiceErrors(e)
                );
    }

    private void handleServiceErrors(Throwable e) {
        Log.e(TAG, "Error in getting list of arrival times", e);
        Utils.showAlertDialog(this, "Error", "Please check your data connection");
    }

        private void handleWidgetCreation(List<TransLocArrival> arrivals, ArrivalTimeWidget atw) {
        if(arrivals != null) {
            // Calculate next arrival time
            if(arrivals.isEmpty()) {
                Log.d(TAG, "arrivals is empty");
                atw.setMinutesUntilArrival(-1);
            } else {
                TransLocArrival nextArrival = arrivals.get(0);
                int minsTillArrival = getMinsUntilArrival(nextArrival);
                atw.setMinutesUntilArrival(minsTillArrival);
            }

            // Create Widget
            RemoteViews remoteViews = Utils.createRemoteViews(this, atw, appWidgetId);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());
            appWidgetManager.updateAppWidget(appWidgetId, Utils.createRemoteViews(getBaseContext(), atw, appWidgetId));
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

            // Add appWidgetId to same ArrivalTimeWidget that is stored in device storage
            try {
                ArrayList<ArrivalTimeWidget> listOfWidgets = Utils.getArrivalTimeWidgetsFromStorage(this);
                for(int i = 0; i < listOfWidgets.size(); i++) {
                    ArrivalTimeWidget widget = listOfWidgets.get(i);
                    if (atw.equals(widget)){
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
            Toast.makeText(getApplicationContext(), "Tap on the widget to update!", Toast.LENGTH_LONG ).show();
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

}
