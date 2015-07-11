package com.shyamu.translocwidget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.shyamu.translocwidget.bl.ArrivalTimeCalculator;
import com.shyamu.translocwidget.fragments.WidgetListFragment;



public class WidgetConfigActivity extends Activity implements WidgetListFragment.OnFragmentInteractionListener {

    private int appWidgetId = 0;

    private static final String TAG = "WidgetConfigActivity";
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
                startActivityForResult(intent, 100);
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
        Log.d(TAG, "inOnFragmentInteraction");
        ArrivalTimeCalculator arrivalTimeCalculator = new ArrivalTimeCalculator(this, incomingAtw);
        ArrivalTimeWidget atw = arrivalTimeCalculator.getArrivalTimeWidgetWithUpdatedTime();
        handleCreationOfWidget(atw);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            if(resultCode == 1) {
                ArrivalTimeWidget atw = (ArrivalTimeWidget) data.getSerializableExtra("atw");
                handleCreationOfWidget(atw);
            } else {
                // error
            }
        }
    }

    private void handleCreationOfWidget(ArrivalTimeWidget atw) {
        Log.d(TAG, "in handleCreationOfWidget with widget: " + atw.toString());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());
        appWidgetManager.updateAppWidget(appWidgetId, Utils.createRemoteViews(getBaseContext(), atw, appWidgetId));
        Bundle bundle = new Bundle();
        bundle.putSerializable("atw", atw);
        appWidgetManager.updateAppWidgetOptions(appWidgetId, bundle);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        Toast.makeText(getApplicationContext(), "Tap on the widget to update!", Toast.LENGTH_LONG ).show();
        finish();
    }

}
