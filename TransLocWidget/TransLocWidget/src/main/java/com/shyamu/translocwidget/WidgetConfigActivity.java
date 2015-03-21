package com.shyamu.translocwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shyamu.translocwidget.fragments.WidgetListFragment;
import com.shyamu.translocwidget.rest.model.TransLocArrival;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.shyamu.translocwidget.Utils.TransLocDataType.ARRIVAL;
import static com.shyamu.translocwidget.Utils.TransLocDataType.ROUTE;


public class WidgetConfigActivity extends Activity implements WidgetListFragment.OnFragmentInteractionListener {

    private int appWidgetId = 0;

    private static final String TAG = "WidgetConfigActivity";
    private ArrivalTimeWidget atw;
    Button addNewWidgetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        addNewWidgetButton = (Button) findViewById(R.id.bAddNewWidget);
        addNewWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WidgetListActivity.class);
                intent.putExtra("starting_fragment", "AddAgencyFragment");
                startActivity(intent);
            }
        });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new WidgetListFragment())
                    .commit();
        }
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
        this.atw = incomingAtw;
        Log.d(TAG, "inOnFragmentInteraction");
        TransLocClient client =
                ServiceGenerator.createService(TransLocClient.class,
                        Utils.BASE_URL,
                        getString(R.string.mashape_key),
                        atw.getAgencyID(),
                        ARRIVAL);
        client.arrivalEstimates(atw.getAgencyID(), atw.getRouteID(), atw.getStopID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::createWidget,
                        e -> Log.e(TAG, "Error in getting list of arrival times", e)
                );
    }

    private void createWidget(List<TransLocArrival> arrivals) {
        if(arrivals != null && !arrivals.isEmpty()) {
            TransLocArrival nextArrival = arrivals.get(0);

            int minsTillArrival = getMinsUntilArrival(nextArrival);
            atw.setMinutesUntilArrival(minsTillArrival);

            handleCreationOfWidget();

        } else {
            Log.e(TAG, "arrivals is null or empty!");
        }
    }

    private int getMinsUntilArrival(TransLocArrival arrival) {
        DateTime currentDate = new DateTime();
        DateTime arrivalDate = new DateTime(arrival.arrivalAt);
        atw.setNextArrivalTime(arrivalDate);
        return Utils.getMinutesBetweenTimes(currentDate, arrivalDate);
    }

    private void handleCreationOfWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());
        appWidgetManager.updateAppWidget(appWidgetId, atw.createRemoteViews(getBaseContext()));

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        Toast.makeText(getApplicationContext(), "Tap on the widget to update!", Toast.LENGTH_LONG ).show();
        finish();
    }

}
